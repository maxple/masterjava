package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectDao implements AbstractDao {

    @SqlUpdate("insert into projects (name, description) values (:name, :description)")
    public abstract int create(@BindBean Project project);

    @SqlQuery("select * from projects where name = :name")
    public abstract Project get(@Bind String name);

    @SqlQuery("select * from projects ORDER BY name LIMIT :it")
    public abstract List<Project> getWithLimit(@Bind int limit);

    @SqlQuery("select * from projects order by name")
    public abstract List<Project> getAll();

    @SqlUpdate("update projects set description = :description where name = :name")
    public abstract int update(@BindBean Project project);

    @SqlUpdate("delete from projects where name = :name")
    public abstract int delete(@Bind String name);

    @SqlUpdate("truncate projects cascade")
    @Override
    public abstract void clean();
}
