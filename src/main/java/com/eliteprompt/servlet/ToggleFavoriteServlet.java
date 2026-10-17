package com.eliteprompt.servlet;

import com.eliteprompt.dao.PromptDAO;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Flips the favorite star (ownership checked in DAO). */
@WebServlet("/toggle-favorite")
public class ToggleFavoriteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        int userId = AuthUtil.currentUserId(req.getSession(false));
        int id = 0;
        try { id = Integer.parseInt(req.getParameter("id")); } catch (Exception ignored) {}

        try {
            if (id > 0 && new PromptDAO().toggleFavorite(id, userId)) {
                req.getSession().setAttribute("flashSuccess", "Favorite updated!");
            } else {
                req.getSession().setAttribute("flashError", "Prompt not found.");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Update failed: " + e.getMessage());
        }
        String back = req.getParameter("from");
        resp.sendRedirect(back != null && back.equals("dashboard") ? "dashboard" : "my-prompts");
    }
}
