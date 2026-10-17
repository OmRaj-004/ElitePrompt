package com.eliteprompt.servlet;

import com.eliteprompt.util.AuthUtil;
import com.eliteprompt.util.PromptGenerator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Shows generator form (GET) and builds prompt server-side (POST). */
@WebServlet("/generator")
public class GeneratorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        req.getRequestDispatcher("/prompt_generator.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        String title = req.getParameter("title");
        String category = req.getParameter("category");
        String requirement = req.getParameter("requirement");
        String tone = req.getParameter("tone");
        String outputFormat = req.getParameter("output_format");

        String generated = "";
        int score = 0;
        java.util.List<String> tips = new java.util.ArrayList<>();
        if (requirement != null && !requirement.trim().isEmpty()) {
            generated = PromptGenerator.generate(category, requirement, tone, outputFormat);
            score = PromptGenerator.score(requirement);
            tips = PromptGenerator.tips(requirement);
        }

        // Keep values so JSP can re-show them
        req.setAttribute("title", title);
        req.setAttribute("category", category);
        req.setAttribute("requirement", requirement);
        req.setAttribute("tone", tone);
        req.setAttribute("output_format", outputFormat);
        req.setAttribute("generated", generated);
        req.setAttribute("score", score);
        req.setAttribute("tips", tips);
        req.getRequestDispatcher("/prompt_generator.jsp").forward(req, resp);
    }
}
