package ru.javaops.masterjava.xml.util;

import j2html.tags.ContainerTag;
import ru.javaops.masterjava.xml.schema.User;

import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import static j2html.TagCreator.*;

public class Util {

    private static JaxbParser userParser = new JaxbParser(User.class);

    public static String getUsersHtml(InputStream inputStream) throws Exception {

        try {
            StaxStreamProcessor processor = new StaxStreamProcessor(inputStream);

            Set<User> users = new TreeSet<>(Comparator.comparing(User::getValue).thenComparing(User::getEmail));

            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                users.add(userParser.unmarshal(processor.getReader(), User.class));
            }

            final ContainerTag table = table().with(
                    tr().with(th("FullName"), th("email"), th("flag")))
                    .attr("border", "1")
                    .attr("cellpadding", "8")
                    .attr("cellspacing", "0");

            users.forEach(u -> table.with(tr().with(td(u.getValue()), td(u.getEmail()), td(u.getFlag().value()))));

            return html().with(
                    head().with(title("Users")),
                    body().with(h1("Users"), table)
            ).render();
        } finally {
            inputStream.close();
        }
    }
}
