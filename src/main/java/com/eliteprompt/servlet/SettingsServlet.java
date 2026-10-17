package com.eliteprompt.servlet;

import com.eliteprompt.dao.UserDAO;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Settings page - save your own OpenCode Zen API key. */
@WebServlet("/settings")
public class SettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        try {
            String key = new UserDAO().getZenApiKey(AuthUtil.currentUserId(req.getSession(false)));
            req.setAttribute("hasKey", key != null && !key.isEmpty());
            // show only last 4 chars so the full key never renders in HTML
            req.setAttribute("keyHint", (key != null && key.length() > 4)
                    ? "••••" + key.substring(key.length() - 4) : "");
        } catch (Exception e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/settings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        int userId = AuthUtil.currentUserId(req.getSession(false));
        try {
            if (req.getParameter("clear_key") != null) {
                new UserDAO().saveZenApiKey(userId, "");
                req.setAttribute("success", "API key removed.");
            } else {
                String key = req.getParameter("zen_api_key");
                if (key == null || key.trim().isEmpty()) {
                    req.setAttribute("error", "Paste your Zen API key first.");
                } else if (!new UserDAO().saveZenApiKey(userId, key)) {
                    req.setAttribute("error", "Could not save. Try again.");
                } else {
                    req.setAttribute("success", "API key saved! You can now use Run with AI.");
                }
            }
            String saved = new UserDAO().getZenApiKey(userId);
            req.setAttribute("hasKey", saved != null && !saved.isEmpty());
            req.setAttribute("keyHint", (saved != null && saved.length() > 4)
                    ? "••••" + saved.substring(saved.length() - 4) : "");
        } catch (Exception e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/settings.jsp").forward(req, resp);
    }
}
