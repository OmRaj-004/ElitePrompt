package com.eliteprompt.servlet;

import com.eliteprompt.dao.PromptDAO;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Deletes prompt securely (only if it belongs to current user). */
@WebServlet("/delete-prompt")
public class DeletePromptServlet extends HttpServlet {
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
            if (id > 0 && new PromptDAO().delete(id, userId)) {
                req.getSession().setAttribute("flashSuccess", "Prompt deleted successfully!");
            } else {
                req.getSession().setAttribute("flashError", "Prompt not found or already deleted.");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Delete failed: " + e.getMessage());
        }
        resp.sendRedirect("my-prompts");
    }
}
