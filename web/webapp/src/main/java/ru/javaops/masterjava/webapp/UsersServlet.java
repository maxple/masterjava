package ru.javaops.masterjava.webapp;

import com.google.common.collect.ImmutableMap;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.service.mail.Addressee;
import ru.javaops.masterjava.service.mail.MailWSClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet("")
public class UsersServlet extends HttpServlet {
    private UserDao userDao = DBIProvider.getDao(UserDao.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                ImmutableMap.of("users", userDao.getWithLimit(20)));
        engine.process("users", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String subject = "";
        String body = "";
        Set<Addressee> addresseeSet = new HashSet<>();

        for (String s : Collections.list(req.getParameterNames())) {
            switch (s) {
                case "subject":
                    subject = req.getParameterValues(s)[0];
                    break;
                case "body":
                    body = req.getParameterValues(s)[0];
                    break;
                default:
                    addresseeSet.add(new Addressee(s));
                    break;
            }
        }

        if (addresseeSet.size() < 1) return;

        MailWSClient.sendToGroup(addresseeSet, new HashSet<>(), subject, body);
    }
}
