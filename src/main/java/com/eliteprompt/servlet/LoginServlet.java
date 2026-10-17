package com.eliteprompt.servlet;

import com.eliteprompt.dao.UserDAO;
import com.eliteprompt.model.User;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Handles login with username/email + password, sets session. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("dashboard");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String loginInput = req.getParameter("login_input");
        String password = req.getParameter("password");

        String error = null;
        if (loginInput == null || loginInput.trim().isEmpty()
                || password == null || password.isEmpty()) {
            error = "Please enter username/email and password.";
        } else {
            try {
                // Try the normal users table first
                User user = new UserDAO().login(loginInput.trim(), password);
                if (user != null) {
                    HttpSession session = req.getSession(true);
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("username", user.getUsername());
                    session.setAttribute("fullName", user.getFullName());
                    session.setAttribute("isAdmin", false);
                    resp.sendRedirect("dashboard");
                    return;
                }
                // Then try the SEPARATE admins table (admin credentials are not in users)
                com.eliteprompt.model.Admin admin =
                        new com.eliteprompt.dao.AdminDAO().findMatching(loginInput.trim(), password);
                if (admin != null) {
                    HttpSession session = req.getSession(true);
                    session.setAttribute("userId", admin.getId());
                    session.setAttribute("username", admin.getUsername());
                    session.setAttribute("fullName", admin.getFullName());
                    session.setAttribute("isAdmin", true);
                    resp.sendRedirect("dashboard");
                    return;
                }
                error = "Invalid username/email or password.";
            } catch (Exception e) {
                error = "Database error: " + e.getMessage();
            }
        }

        req.setAttribute("error", error);
        req.setAttribute("login_input", loginInput);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }
}
