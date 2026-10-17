package com.eliteprompt.servlet;

import com.eliteprompt.dao.PromptDAO;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Copies a public prompt into the logged-in user's library. */
@WebServlet("/clone-prompt")
public class ClonePromptServlet extends HttpServlet {
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
            if (id > 0 && new PromptDAO().clonePrompt(id, userId)) {
                req.getSession().setAttribute("flashSuccess", "Prompt cloned to your library!");
                resp.sendRedirect("my-prompts");
            } else {
                req.getSession().setAttribute("flashError", "Prompt not found or not public.");
                resp.sendRedirect("gallery");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Clone failed: " + e.getMessage());
            resp.sendRedirect("gallery");
        }
    }
}
