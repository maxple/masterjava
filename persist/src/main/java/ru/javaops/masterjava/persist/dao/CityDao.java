package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {

    @SqlUpdate("insert into cities (id, name) values (:id, :name)")
    public abstract int create(@BindBean City city);

    @SqlQuery("select * from cities where id = :id")
    public abstract City get(@Bind String id);

    @SqlQuery("select * from cities ORDER BY id LIMIT :it")
    public abstract List<City> getWithLimit(@Bind int limit);

    @SqlQuery("select * from cities order by id")
    public abstract List<City> getAll();

    @SqlUpdate("update cities set name = :name where id = :id")
    public abstract int update(@BindBean City city);

    @SqlUpdate("delete from cities where id = :id")
    public abstract int delete(@Bind String id);

    @SqlUpdate("truncate cities cascade")
    @Override
    public abstract void clean();
}
