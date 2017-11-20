package ru.javaops.masterjava.persist.dao;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.data.CityTestData;

import static ru.javaops.masterjava.persist.data.CityTestData.*;

public class CityDaoTest extends AbstractDaoTest<CityDao> {

    public CityDaoTest() {
        super(CityDao.class);
    }

    @BeforeClass
    public static void init() throws Exception {
        CityTestData.init();
    }

    @Before
    public void setUp() throws Exception {
        CityTestData.setUp();
    }

    @Test
    public void getWithLimit() {
        Assert.assertEquals(ImmutableList.of(KIV, MNSK, MOW), dao.getWithLimit(3));
    }

    @Test
    public void create() throws Exception {
        dao.clean();
        dao.create(SPB);
        Assert.assertEquals(1, dao.getWithLimit(100).size());
    }
}