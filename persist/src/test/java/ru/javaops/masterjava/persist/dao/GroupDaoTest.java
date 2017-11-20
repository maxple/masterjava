package ru.javaops.masterjava.persist.dao;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.data.GroupTestData;

import static ru.javaops.masterjava.persist.data.GroupTestData.*;

public class GroupDaoTest extends AbstractDaoTest<GroupDao> {

    public GroupDaoTest() {
        super(GroupDao.class);
    }

    @BeforeClass
    public static void init() throws Exception {
        GroupTestData.init();
    }

    @Before
    public void setUp() throws Exception {
        GroupTestData.setUp();
    }

    @Test
    public void getWithLimit() {
        Assert.assertEquals(ImmutableList.of(MASTERJAVA01, TOPJAVA06, TOPJAVA07), dao.getWithLimit(3));
    }

    @Test
    public void create() throws Exception {
        dao.clean();
        dao.create(TOPJAVA06);
        Assert.assertEquals(1, dao.getWithLimit(100).size());
    }
}