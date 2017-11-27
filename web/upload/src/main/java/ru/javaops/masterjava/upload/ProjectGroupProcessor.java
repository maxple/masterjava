package ru.javaops.masterjava.upload;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.persist.model.type.GroupType;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
public class ProjectGroupProcessor {
    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    public Map<String, Group> process(StaxStreamProcessor processor) throws XMLStreamException {
        val map = projectDao.getAsMap();
        val newGroups = new ArrayList<Group>();

        while (processor.startElement("Project", "Projects")) {
            val name = processor.getAttribute("name");

            final Project project;
            if (!map.containsKey(name)) {
                project = new Project(name, processor.startElement("description", "Project") ? processor.getText() : "");
                projectDao.insert(project);
            } else {
                project = map.get(name);
            }

            final Integer projectId = project.getId();
            while (processor.startElement("Group", "Project")) {
                newGroups.add(new Group(processor.getAttribute("name"), GroupType.fromValue(processor.getAttribute("type")), projectId));
            }
        }

        log.info("Insert batch " + newGroups);
        groupDao.insertBatch(newGroups);
        return groupDao.getAsMap();
    }
}
