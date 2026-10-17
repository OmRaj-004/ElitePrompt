package com.eliteprompt.servlet;

import com.eliteprompt.dao.PromptDAO;
import com.eliteprompt.model.Prompt;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Edit saved prompt (checks ownership via user_id). */
@WebServlet("/edit-prompt")
public class EditPromptServlet extends HttpServlet {

    private int parseId(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        int userId = AuthUtil.currentUserId(req.getSession(false));
        int id = parseId(req.getParameter("id"));
        try {
            Prompt p = new PromptDAO().findById(id, userId);
            if (p == null) {
                req.getSession().setAttribute("flashError", "Prompt not found.");
                resp.sendRedirect("my-prompts");
                return;
            }
            req.setAttribute("prompt", p);
            req.getRequestDispatcher("/edit_prompt.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        int userId = AuthUtil.currentUserId(req.getSession(false));
        int id = parseId(req.getParameter("id"));

        String title = req.getParameter("title");
        String category = req.getParameter("category");
        String requirement = req.getParameter("requirement");
        String tone = req.getParameter("tone");
        String outputFormat = req.getParameter("output_format");
        String generated = req.getParameter("generated_prompt");
        String tags = req.getParameter("tags");
        boolean isPublic = req.getParameter("is_public") != null;
        if (tags == null) tags = "";

        if (title == null || title.trim().isEmpty()
                || requirement == null || requirement.trim().isEmpty()
                || generated == null || generated.trim().isEmpty()) {
            req.setAttribute("error", "Title, requirement and generated prompt are required.");
            try {
                req.setAttribute("prompt", new PromptDAO().findById(id, userId));
            } catch (Exception e) { throw new ServletException(e); }
            req.getRequestDispatcher("/edit_prompt.jsp").forward(req, resp);
            return;
        }

        try {
            Prompt p = new Prompt();
            p.setId(id);
            p.setUserId(userId);
            p.setTitle(title.trim());
            p.setCategory(category);
            p.setRequirement(requirement.trim());
            p.setTone(tone);
            p.setOutputFormat(outputFormat);
            p.setGeneratedPrompt(generated.trim());
            p.setPublic(isPublic);
            p.setTags(tags.trim());

            new PromptDAO().update(p);
            req.getSession().setAttribute("flashSuccess", "Prompt updated successfully!");
            resp.sendRedirect("my-prompts");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
