package ru.javaops.masterjava.service.mail;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.MailResultDao;
import ru.javaops.masterjava.persist.model.MailResult;
import ru.javaops.masterjava.persist.model.type.MailState;

import java.sql.DriverManager;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MailSender {
    private static final MailResultDao mailResultDao;

    static {
        Config db = Configs.getConfig("persist.conf", "db");

        DBIProvider.init(() -> {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("PostgreSQL driver not found", e);
            }
            return DriverManager.getConnection(db.getString("url"), db.getString("user"), db.getString("password"));
        });

        mailResultDao = DBIProvider.getDao(MailResultDao.class);
    }

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));

        Config mailConfig = Configs.getConfig("mail.conf", "mail");

        final String from = mailConfig.getString("username");

        MailResult mailResult = new MailResult();

        mailResult.setFromEmail(from);

        final Iterable<String> toEmails = toEmails(to);
        final Iterable<String> ccEmails = toEmails(cc);

        mailResult.setToEmails(Joiner.on(",").join(toEmails));
        mailResult.setCcEmails(Joiner.on(",").join(ccEmails));

        mailResult.setSubject(subject);
        mailResult.setBody(body);

        try {
            Email email = new SimpleEmail();

            final String host = mailConfig.getString("host");

            email.setHostName(host);
            email.setSmtpPort(mailConfig.getInt("port"));

            email.setAuthenticator(new DefaultAuthenticator(from, mailConfig.getString("password")));

            email.setSSLOnConnect(mailConfig.getBoolean("useSSL"));
            email.setStartTLSEnabled(mailConfig.getBoolean("useTLS"));

            email.setDebug(mailConfig.getBoolean("debug"));

            email.setFrom(from);
            email.setSubject(subject);
            email.setMsg(body);

            for (String m : toEmails) {
                email.addTo(m);
            }

            for (String m : ccEmails) {
                email.addCc(m);
            }

            email.getMailSession().getProperties().put("mail.smtp.ssl.trust", host);

            mailResult.setState(MailState.SENDING);
            mailResultDao.insert(mailResult);

            email.send();

            mailResult.setState(MailState.SENT);
            mailResultDao.update(mailResult);
        } catch (Exception e) {
            log.error("", e);

            mailResult.setState(MailState.NOT_SENT);
            mailResult.setStateMessage(e.toString());
            mailResultDao.update(mailResult);
        }
    }

    private static List<String> toEmails(List<Addressee> list) {
        return list == null ? Collections.emptyList() : StreamEx.of(list).map(a -> a == null ? null : Strings.isNullOrEmpty(a.getEmail()) ? null : a.getEmail()).filter(Objects::nonNull).toList();
    }
}
