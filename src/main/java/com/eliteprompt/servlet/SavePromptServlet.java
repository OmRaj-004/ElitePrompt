package com.eliteprompt.servlet;

import com.eliteprompt.dao.PromptDAO;
import com.eliteprompt.model.Prompt;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Saves generated prompt into database. */
@WebServlet("/save-prompt")
public class SavePromptServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        int userId = AuthUtil.currentUserId(req.getSession(false));

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
            req.getSession().setAttribute("flashError", "Title, requirement and generated prompt are required.");
            resp.sendRedirect("generator");
            return;
        }

        try {
            Prompt p = new Prompt();
            p.setUserId(userId);
            p.setTitle(title.trim());
            p.setCategory(category);
            p.setRequirement(requirement.trim());
            p.setTone(tone);
            p.setOutputFormat(outputFormat);
            p.setGeneratedPrompt(generated.trim());
            p.setFavorite(false);
            p.setPublic(isPublic);
            p.setTags(tags.trim());

            if (new PromptDAO().insert(p)) {
                req.getSession().setAttribute("flashSuccess", "Prompt saved successfully!");
                resp.sendRedirect("my-prompts");
            } else {
                throw new Exception("Insert failed");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("generator");
    }
}
