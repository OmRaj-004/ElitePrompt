package com.eliteprompt.servlet;

import com.eliteprompt.dao.FeedbackDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Feedback/contact form - saves message into PostgreSQL. */
@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/feedback.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String subject = req.getParameter("subject");
        String message = req.getParameter("message");

        String error = null;
        if (name == null || name.trim().isEmpty()) error = "Name is required.";
        else if (email == null || !email.contains("@")) error = "Valid email is required.";
        else if (subject == null || subject.trim().isEmpty()) error = "Subject is required.";
        else if (message == null || message.trim().isEmpty()) error = "Message is required.";

        if (error == null) {
            try {
                if (new FeedbackDAO().save(name.trim(), email.trim(), subject.trim(), message.trim())) {
                    req.setAttribute("success", "Thank you! Your feedback has been submitted.");
                    req.getRequestDispatcher("/feedback.jsp").forward(req, resp);
                    return;
                } else {
                    error = "Failed to submit. Try again.";
                }
            } catch (Exception e) {
                error = "Database error: " + e.getMessage();
            }
        }

        req.setAttribute("error", error);
        req.setAttribute("name", name);
        req.setAttribute("email", email);
        req.setAttribute("subject", subject);
        req.setAttribute("message", message);
        req.getRequestDispatcher("/feedback.jsp").forward(req, resp);
    }
}
