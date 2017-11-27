package ru.javaops.masterjava.upload;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserGroup;
import ru.javaops.masterjava.persist.model.type.UserFlag;
import ru.javaops.masterjava.upload.PayloadProcessor.FailedEmails;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class UserProcessor {
    private static final int NUMBER_THREADS = 4;

    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static UserDao userDao = DBIProvider.getDao(UserDao.class);
    private static UserGroupDao userGroupDao = DBIProvider.getDao(UserGroupDao.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    /*
     * return failed users chunks
     */
    public List<FailedEmails> process(final StaxStreamProcessor processor, Map<String, City> cities, Map<String, Group> groups, int chunkSize) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize=" + chunkSize);

        Map<String, Future<List<String>>> chunkFutures = new LinkedHashMap<>();  // ordered map (emailRange -> chunk future)

        int id = userDao.getSeqAndSkip(chunkSize);

        List<User> chunk = new ArrayList<>(chunkSize);
        Map<User, List<UserGroup>> userGroupChunkMap = new HashMap<>();

        val unmarshaller = jaxbParser.createUnmarshaller();
        List<FailedEmails> failed = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            String cityRef = processor.getAttribute("city");  // unmarshal doesn't get city ref
            String groupRefs = processor.getAttribute("groupRefs");  // unmarshal doesn't get groupRefs

            ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);

            final Set<String> groupRefSet = Strings.isNullOrEmpty(groupRefs) ? Collections.emptySet() : new HashSet<>(Splitter.on(" ").splitToList(groupRefs));

            Set<String> diffGroupRefs = Sets.difference(groupRefSet, groups.keySet());

            final String email = xmlUser.getEmail();

            if (cities.get(cityRef) == null) {
                failed.add(new FailedEmails(email, "City '" + cityRef + "' is not present in DB"));
            } else if (diffGroupRefs.size() > 0) {
                failed.add(new FailedEmails(email, "Groups '" + diffGroupRefs + "' are not present in DB"));
            } else {
                final User user = new User(id, xmlUser.getValue(), email, UserFlag.valueOf(xmlUser.getFlag().value()), cityRef);
                chunk.add(user);

                final ArrayList<UserGroup> userGroupChunk = new ArrayList<>();
                userGroupChunkMap.put(user, userGroupChunk);

                int finalId = id++;
                userGroupChunk.addAll(StreamEx.of(groupRefSet).map(g -> new UserGroup(finalId, groups.get(g).getId())).toList());

                if (chunk.size() == chunkSize) {
                    addChunkFutures(chunkFutures, chunk, userGroupChunkMap);
                    chunk = new ArrayList<>(chunkSize);
                    userGroupChunkMap = new HashMap<>();
                    id = userDao.getSeqAndSkip(chunkSize);
                }
            }
        }

        if (!chunk.isEmpty()) {
            addChunkFutures(chunkFutures, chunk, userGroupChunkMap);
        }

        List<String> allAlreadyPresents = new ArrayList<>();
        chunkFutures.forEach((emailRange, future) -> {
            try {
                List<String> alreadyPresentsInChunk = future.get();
                log.info("{} successfully executed with already presents: {}", emailRange, alreadyPresentsInChunk);
                allAlreadyPresents.addAll(alreadyPresentsInChunk);
            } catch (InterruptedException | ExecutionException e) {
                log.error(emailRange + " failed", e);
                failed.add(new FailedEmails(emailRange, e.toString()));
            }
        });
        if (!allAlreadyPresents.isEmpty()) {
            failed.add(new FailedEmails(allAlreadyPresents.toString(), "already presents"));
        }
        return failed;
    }

    private void addChunkFutures(Map<String, Future<List<String>>> chunkFutures, List<User> chunk, Map<User, List<UserGroup>> userGroupChunkMap) {
        String emailRange = String.format("[%s-%s]", chunk.get(0).getEmail(), chunk.get(chunk.size() - 1).getEmail());
        Future<List<String>> future = executorService.submit(() -> {
            final List<String> conflictEmails = userDao.insertAndGetConflictEmails(chunk);

            for (User user : userGroupChunkMap.keySet()) {
                if (!conflictEmails.contains(user.getEmail())) {
                    userGroupDao.insertBatch(userGroupChunkMap.get(user));
                }
            }

            return conflictEmails;
        });
        chunkFutures.put(emailRange, future);
        log.info("Submit chunk: " + emailRange);
    }
}
