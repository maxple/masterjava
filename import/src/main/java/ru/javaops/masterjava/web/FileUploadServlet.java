package ru.javaops.masterjava.web;

import ru.javaops.masterjava.xml.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/upload")
@MultipartConfig(location = "D:/tmp")
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/upload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = "";
        for (Part part : request.getParts()) {
            fileName = part.getSubmittedFileName();
            part.write(fileName);
            break;
        }
        PrintWriter out = response.getWriter();
        try {
            out.print(Util.getUsersHtml("D:/tmp/" + fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
