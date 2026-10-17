package com.eliteprompt.servlet;

import com.eliteprompt.dao.AdminDAO;
import com.eliteprompt.dao.UserDAO;
import com.eliteprompt.model.Admin;
import com.eliteprompt.model.User;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/** Admin dashboard: monitor users (users table) and admins (admins table). */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Only admins may open the dashboard
        if (!AuthUtil.requireAdmin(req, resp)) return;

        try {
            UserDAO userDao = new UserDAO();
            List<User> users = userDao.findAll();
            for (User u : users) {
                u.setPromptCount(userDao.countPromptsOf(u.getId()));
            }
            req.setAttribute("users", users);
            req.setAttribute("totalUsers", userDao.countAll());

            // Admins live in their own table
            List<Admin> admins = new AdminDAO().findAll();
            req.setAttribute("admins", admins);
            req.setAttribute("adminCount", admins.size());
        } catch (Exception e) {
            req.setAttribute("error", "Database error: " + e.getMessage());
        }

        // Flash messages from admin actions
        HttpSession s = req.getSession(false);
        if (s != null) {
            req.setAttribute("adminFlashSuccess", s.getAttribute("adminFlashSuccess"));
            req.setAttribute("adminFlashError", s.getAttribute("adminFlashError"));
            s.removeAttribute("adminFlashSuccess");
            s.removeAttribute("adminFlashError");
        }
        req.getRequestDispatcher("/admin.jsp").forward(req, resp);
    }
}
