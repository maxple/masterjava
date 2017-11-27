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
import ru.javaops.masterjava.persist.DBITestProvider;
import ru.javaops.masterjava.persist.dao.MailResultDao;
import ru.javaops.masterjava.persist.model.MailResult;
import ru.javaops.masterjava.persist.model.type.MailState;

import javax.mail.Authenticator;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MailSender {
    static {
        Config mailConfig = Configs.getConfig("mail.conf", "mail");

        host = mailConfig.getString("host");
        port = mailConfig.getInt("port");
        username = mailConfig.getString("username");
        authenticator = new DefaultAuthenticator(mailConfig.getString("username"), mailConfig.getString("password"));
        useSSL = mailConfig.getBoolean("useSSL");
        useTLS = mailConfig.getBoolean("useTLS");
        debug = mailConfig.getBoolean("debug");

        DBITestProvider.initDBI();
    }

    private static String host;
    private static int port;
    private static String username;
    private static Authenticator authenticator;
    private static boolean useSSL;
    private static boolean useTLS;
    private static boolean debug;

    private static final MailResultDao mailResultDao = DBIProvider.getDao(MailResultDao.class);

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));

        MailResult mailResult = new MailResult();

        mailResult.setFromEmail(username);

        final Iterable<String> toEmails = toEmails(to);
        final Iterable<String> ccEmails = toEmails(cc);

        mailResult.setToEmails(Joiner.on(",").join(toEmails));
        mailResult.setCcEmails(Joiner.on(",").join(ccEmails));

        mailResult.setSubject(subject);
        mailResult.setBody(body);

        try {
            Email email = new SimpleEmail();

            email.setHostName(host);
            email.setSmtpPort(port);

            email.setAuthenticator(authenticator);

            email.setSSLOnConnect(useSSL);
            email.setStartTLSEnabled(useTLS);

            email.setDebug(debug);

            email.setFrom(username);
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
