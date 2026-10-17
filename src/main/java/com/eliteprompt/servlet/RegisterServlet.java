package com.eliteprompt.servlet;

import com.eliteprompt.dao.UserDAO;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Handles registration form (GET shows page, POST processes). */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("dashboard");
            return;
        }
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fullName = req.getParameter("full_name");
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm_password");

        String error = null;
        if (fullName == null || fullName.trim().isEmpty()) error = "Full name is required.";
        else if (username == null || username.trim().isEmpty()) error = "Username is required.";
        else if (email == null || !email.contains("@")) error = "Valid email is required.";
        else if (password == null || password.length() < 6) error = "Password must be at least 6 characters.";
        else if (!password.equals(confirm)) error = "Passwords do not match.";

        if (error == null) {
            try {
                UserDAO dao = new UserDAO();
                if (dao.exists(username.trim(), email.trim())) {
                    error = "Username or email already exists.";
                } else if (dao.register(fullName.trim(), username.trim(), email.trim(), password)) {
                    req.setAttribute("success", "Registration successful! Please login.");
                    req.getRequestDispatcher("/register.jsp").forward(req, resp);
                    return;
                } else {
                    error = "Registration failed. Try again.";
                }
            } catch (Exception e) {
                error = "Database error: " + e.getMessage();
            }
        }

        // On error, keep entered values and show message
        req.setAttribute("error", error);
        req.setAttribute("full_name", fullName);
        req.setAttribute("username", username);
        req.setAttribute("email", email);
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }
}
