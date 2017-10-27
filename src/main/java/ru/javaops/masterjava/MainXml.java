package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

public class MainXml {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static void main(String[] args) throws IOException, JAXBException, XMLStreamException {
        System.out.println(getProjectUserNamesJaxb("topjava"));
        System.out.println(getProjectUserNamesStax("topjava"));
    }

    /**
     * Возвращает отсортированный список участников заданного проекта (через JAXB)
     *
     * @param projectName имя проекта
     * @return отсортированный список участников
     */
    public static List<String> getProjectUserNamesJaxb(String projectName) throws IOException, JAXBException {

        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource("payload.xml").openStream());

        Project project = null;

        for (Project p : payload.getProjects().getProject()) {
            if (projectName.equals(p.getId())) {
                project = p;
                break;
            }
        }

        if (project == null) {
            return Collections.emptyList();
        }

        List<String> groupIds = new ArrayList<>();

        for (Group group : project.getGroups().getGroup()) {
            groupIds.add(group.getId());
        }

        Set<User> userSet = new TreeSet<>();

        for (User user : payload.getUsers().getUser()) {
            user.getGroupId().retainAll(groupIds);
            if (user.getGroupId().size() > 0) {
                userSet.add(user);
            }
        }

        return getUserNamesEmails(userSet);
    }

    /**
     * Возвращает отсортированный список участников заданного проекта (через StAX)
     *
     * @param projectName имя проекта
     * @return отсортированный список участников
     */
    public static List<String> getProjectUserNamesStax(String projectName) throws IOException, XMLStreamException {

        Set<User> users = new HashSet<>();
        List<String> groupIds = new ArrayList<>();

        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {

            while (processor.startElement("User", "Users")) {
                User user = new User();
                users.add(user);
                user.setEmail(processor.getAttribute("email"));
                if (processor.startElement("fullName", "User")) {
                    user.setFullName(processor.getText());
                }
                while (processor.startElement("groupId", "User")) {
                    user.getGroupId().add(processor.getText());
                }
            }

            while (processor.startElement("Project", "Projects")) {
                if (!projectName.equals(processor.getAttribute("id"))) {
                    continue;
                }
                while (processor.startElement("Groups", "Project")) {
                    while (processor.startElement("Group", "Groups")) {
                        groupIds.add(processor.getAttribute("id"));
                    }
                }
            }
        }

        Set<User> userSet = new TreeSet<>();

        for (User user : users) {
            user.getGroupId().retainAll(groupIds);
            if (user.getGroupId().size() > 0) {
                userSet.add(user);
            }
        }

        return getUserNamesEmails(userSet);
    }

    private static List<String> getUserNamesEmails(Set<User> userSet) {
        List<String> userNamesEmails = new ArrayList<>();

        for (User user : userSet) {
            userNamesEmails.add(user.getFullName() + "/" + user.getEmail());
        }

        return userNamesEmails;
    }
}
