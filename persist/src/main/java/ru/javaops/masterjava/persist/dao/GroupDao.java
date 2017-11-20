package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

    public int create(Group group) {
        return create(group, group.getProject());
    }

    @SqlUpdate("insert into groups (name, gr_type, project_name) values (:g.name, CAST(:g.gr_type AS GROUP_TYPE), :p.name)")
    public abstract int create(@BindBean("g") Group group, @BindBean("p") Project project);

    @SqlQuery("select * from groups where name = :name")
    public abstract Group get(@Bind String name);

    @SqlQuery("select * from groups ORDER BY name LIMIT :it")
    public abstract List<Group> getWithLimit(@Bind int limit);

    @SqlQuery("select * from groups order by name")
    public abstract List<Group> getAll();

    @SqlUpdate("update groups set gr_type = CAST(:gr_type AS GROUP_TYPE) where name = :name")
    public abstract int update(@BindBean Group group);

    @SqlUpdate("delete from groups where name = :name")
    public abstract int delete(@Bind String name);

    @SqlUpdate("truncate groups cascade")
    @Override
    public abstract void clean();
}
