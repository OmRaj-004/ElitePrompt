package com.eliteprompt.servlet;

import com.eliteprompt.dao.PromptDAO;
import com.eliteprompt.model.Prompt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/** Public gallery - anyone (even guests) can browse shared prompts. */
@WebServlet("/gallery")
public class GalleryServlet extends HttpServlet {

    private static final int PAGE_SIZE = 9;

    private int parsePage(String s) {
        try {
            int p = Integer.parseInt(s);
            return p < 1 ? 1 : p;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String search = req.getParameter("search");
        String category = req.getParameter("category");
        if (search == null) search = "";
        if (category == null) category = "";
        int page = parsePage(req.getParameter("page"));

        try {
            PromptDAO dao = new PromptDAO();
            int total = dao.countPublic(search.trim(), category.trim());
            int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
            if (page > totalPages) page = totalPages;
            List<Prompt> prompts = dao.findPublic(search.trim(), category.trim(),
                    PAGE_SIZE, (page - 1) * PAGE_SIZE);

            req.setAttribute("prompts", prompts);
            req.setAttribute("search", search);
            req.setAttribute("filter", category);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("total", total);
        } catch (Exception e) {
            req.setAttribute("error", "Database error: " + e.getMessage());
        }

        HttpSession s = req.getSession(false);
        if (s != null) {
            req.setAttribute("flashSuccess", s.getAttribute("flashSuccess"));
            req.setAttribute("flashError", s.getAttribute("flashError"));
            s.removeAttribute("flashSuccess");
            s.removeAttribute("flashError");
        }
        req.getRequestDispatcher("/gallery.jsp").forward(req, resp);
    }
}
