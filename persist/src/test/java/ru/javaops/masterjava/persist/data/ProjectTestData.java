package ru.javaops.masterjava.persist.data;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Project;

public class ProjectTestData {
    public static Project TOPJAVA;
    public static Project MASTERJAVA;

    public static void init() {
        TOPJAVA = new Project("topjava", "Topjava");
        MASTERJAVA = new Project("masterjava", "Masterjava");
    }

    public static void setUp() {
        ProjectDao dao = DBIProvider.getDao(ProjectDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            dao.create(TOPJAVA);
            dao.create(MASTERJAVA);
        });
    }
}
