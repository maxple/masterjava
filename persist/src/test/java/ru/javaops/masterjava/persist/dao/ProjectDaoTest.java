package ru.javaops.masterjava.persist.dao;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.data.ProjectTestData;

import static ru.javaops.masterjava.persist.data.ProjectTestData.MASTERJAVA;
import static ru.javaops.masterjava.persist.data.ProjectTestData.TOPJAVA;

public class ProjectDaoTest extends AbstractDaoTest<ProjectDao> {

    public ProjectDaoTest() {
        super(ProjectDao.class);
    }

    @BeforeClass
    public static void init() throws Exception {
        ProjectTestData.init();
    }

    @Before
    public void setUp() throws Exception {
        ProjectTestData.setUp();
    }

    @Test
    public void getWithLimit() {
        Assert.assertEquals(ImmutableList.of(MASTERJAVA, TOPJAVA), dao.getWithLimit(2));
    }

    @Test
    public void create() throws Exception {
        dao.clean();
        dao.create(TOPJAVA);
        Assert.assertEquals(1, dao.getWithLimit(100).size());
    }
}