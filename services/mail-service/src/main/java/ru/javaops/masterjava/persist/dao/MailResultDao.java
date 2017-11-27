package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.MailResult;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class MailResultDao implements AbstractDao {

    @SqlUpdate("TRUNCATE mail_results CASCADE")
    @Override
    public abstract void clean();

    @SqlQuery("SELECT * FROM mail_results ORDER BY from_email")
    public abstract List<MailResult> getAll();

    @SqlUpdate("INSERT INTO mail_results (from_email, to_emails, cc_emails, subject, body, state, state_message) VALUES (:fromEmail, :toEmails, :ccEmails, :subject, :body, CAST(:state AS mail_state_type), :stateMessage)")
    @GetGeneratedKeys
    public abstract int insertGeneratedId(@BindBean MailResult mailResult);

    public void insert(MailResult mailResult) {
        mailResult.setId(insertGeneratedId(mailResult));
    }

    @SqlUpdate("update mail_results set from_email = :fromEmail, to_emails = :toEmails, cc_emails = :ccEmails, subject = :subject, body = :body, state = CAST(:state AS mail_state_type), state_message = :stateMessage where id = :id")
    public abstract int update(@BindBean MailResult mailResult);
}
