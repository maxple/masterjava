package ru.javaops.masterjava.persist.data;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;

import static ru.javaops.masterjava.persist.data.ProjectTestData.MASTERJAVA;
import static ru.javaops.masterjava.persist.data.ProjectTestData.TOPJAVA;

public class GroupTestData {
    public static Group TOPJAVA06;
    public static Group TOPJAVA07;
    public static Group TOPJAVA08;
    public static Group MASTERJAVA01;

    public static void init() {
        ProjectTestData.init();
        ProjectTestData.setUp();
        TOPJAVA06 = new Group("topjava06", GroupType.FINISHED, TOPJAVA);
        TOPJAVA07 = new Group("topjava07", GroupType.FINISHED, TOPJAVA);
        TOPJAVA08 = new Group("topjava08", GroupType.CURRENT, TOPJAVA);
        MASTERJAVA01 = new Group("masterjava01", GroupType.CURRENT, MASTERJAVA);
    }

    public static void setUp() {
        GroupDao dao = DBIProvider.getDao(GroupDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            dao.create(TOPJAVA06);
            dao.create(TOPJAVA07);
            dao.create(TOPJAVA08);
            dao.create(MASTERJAVA01);
        });
    }
}
