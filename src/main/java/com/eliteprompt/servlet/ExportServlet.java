package com.eliteprompt.servlet;

import com.eliteprompt.dao.PromptDAO;
import com.eliteprompt.model.Prompt;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/** Downloads the user's prompts as a JSON file. */
@WebServlet("/export")
public class ExportServlet extends HttpServlet {

    // Minimal JSON escaping (keeps the project dependency-free)
    private String json(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        int userId = AuthUtil.currentUserId(req.getSession(false));

        try {
            List<Prompt> prompts = new PromptDAO().findAllByUser(userId);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"elite-prompt-export.json\"");

            PrintWriter out = resp.getWriter();
            out.println("[");
            for (int i = 0; i < prompts.size(); i++) {
                Prompt p = prompts.get(i);
                out.println("  {");
                out.println("    \"title\": \"" + json(p.getTitle()) + "\",");
                out.println("    \"category\": \"" + json(p.getCategory()) + "\",");
                out.println("    \"requirement\": \"" + json(p.getRequirement()) + "\",");
                out.println("    \"tone\": \"" + json(p.getTone()) + "\",");
                out.println("    \"output_format\": \"" + json(p.getOutputFormat()) + "\",");
                out.println("    \"generated_prompt\": \"" + json(p.getGeneratedPrompt()) + "\",");
                out.println("    \"tags\": \"" + json(p.getTags()) + "\",");
                out.println("    \"is_favorite\": " + p.isFavorite() + ",");
                out.println("    \"is_public\": " + p.isPublic());
                out.print("  }" + (i < prompts.size() - 1 ? "," : "") + "\n");
            }
            out.println("]");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Export failed: " + e.getMessage());
        }
    }
}
