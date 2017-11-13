package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserProcessor {
    private static final int THREAD_NUMBER = 4;
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);

    private final UserDao userDao = DBIProvider.getDao(UserDao.class);

    public List<User> process(final InputStream is, final int pageSize, List<String> errorMessages) throws XMLStreamException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        List<User> users = new ArrayList<>(pageSize);
        List<Future<List<User>>> futures = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {

            try {
                final String email = processor.getAttribute("email");
                final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
                final String fullName = processor.getReader().getElementText();
                final User user = new User(fullName, email, flag);
                users.add(user);
            } catch (Exception e) {
                errorMessages.add(e.toString());
                continue;
            }

            if (users.size() >= pageSize) {
                futures.add(save(users, errorMessages));
                users = new ArrayList<>(pageSize);
            }
        }
        if (users.size() > 0) {
            futures.add(save(users, errorMessages));
        }

        List<User> userList = new ArrayList<>();

        for (Future<List<User>> future : futures) {
            try {
                userList.addAll(future.get());
            } catch (Exception e) {
                errorMessages.add(e.toString());
            }
        }

        return userList;
    }

    private Future<List<User>> save(List<User> users, List<String> errorMessages) {
        return executor.submit(() -> {
            try {
                userDao.batchInsertGeneratedId(users);
            } catch (Exception e) {
                errorMessages.add(("Chunk ('" + users.get(0).getEmail() + "' - '" + users.get(users.size() - 1).getEmail() + "') batch insert failed") + ", " + e.toString());
                return Collections.emptyList();
            }
            return users;
        });
    }
}
