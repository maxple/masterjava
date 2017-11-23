package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;
import ru.javaops.masterjava.persist.model.type.MailState;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MailResult extends BaseEntity {

    @NonNull
    @Column("from_email")
    private String fromEmail;

    @NonNull
    @Column("to_emails")
    private String toEmails;

    @NonNull
    @Column("cc_emails")
    private String ccEmails;

    @NonNull
    private String subject;

    @NonNull
    private String body;

    @NonNull
    private MailState state;

    @Column("state_message")
    private String stateMessage;
}
