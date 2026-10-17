package com.eliteprompt.servlet;

import com.eliteprompt.dao.PromptDAO;
import com.eliteprompt.model.Prompt;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/** Lists logged-in user's prompts with search + filters + pagination. */
@WebServlet("/my-prompts")
public class MyPromptsServlet extends HttpServlet {

    private static final int PAGE_SIZE = 6;

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
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        int userId = AuthUtil.currentUserId(req.getSession(false));
        String search = req.getParameter("search");
        String category = req.getParameter("category");
        String tag = req.getParameter("tag");
        boolean favOnly = "1".equals(req.getParameter("fav"));
        if (search == null) search = "";
        if (category == null) category = "";
        if (tag == null) tag = "";
        int page = parsePage(req.getParameter("page"));

        try {
            PromptDAO dao = new PromptDAO();
            int total = dao.countFiltered(userId, search.trim(), category.trim(), tag.trim(), favOnly);
            int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
            if (page > totalPages) page = totalPages;
            List<Prompt> prompts = dao.findByUser(userId, search.trim(), category.trim(),
                    tag.trim(), favOnly, PAGE_SIZE, (page - 1) * PAGE_SIZE);

            req.setAttribute("prompts", prompts);
            req.setAttribute("search", search);
            req.setAttribute("filter", category);
            req.setAttribute("tag", tag);
            req.setAttribute("favOnly", favOnly);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("total", total);
        } catch (Exception e) {
            req.setAttribute("error", "Database error: " + e.getMessage());
        }

        // Flash messages from save/edit/delete
        HttpSession s = req.getSession(false);
        if (s != null) {
            req.setAttribute("flashSuccess", s.getAttribute("flashSuccess"));
            req.setAttribute("flashError", s.getAttribute("flashError"));
            s.removeAttribute("flashSuccess");
            s.removeAttribute("flashError");
        }
        req.getRequestDispatcher("/my_prompts.jsp").forward(req, resp);
    }
}
