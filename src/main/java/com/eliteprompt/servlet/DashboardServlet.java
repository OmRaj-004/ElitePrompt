package com.eliteprompt.servlet;

import com.eliteprompt.dao.PromptDAO;
import com.eliteprompt.model.Prompt;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/** Shows welcome message, stats and recent prompts. */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        int userId = AuthUtil.currentUserId(req.getSession(false));
        try {
            PromptDAO dao = new PromptDAO();
            int total = dao.countByUser(userId);
            int favCount = dao.countFavorites(userId);
            List<Prompt> recent = dao.recentByUser(userId, 5);
            req.setAttribute("total", total);
            req.setAttribute("favCount", favCount);
            req.setAttribute("recent", recent);
        } catch (Exception e) {
            req.setAttribute("error", "Database error: " + e.getMessage());
        }
        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
