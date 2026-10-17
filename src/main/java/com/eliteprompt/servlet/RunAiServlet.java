package com.eliteprompt.servlet;

import com.eliteprompt.ai.AiProvider;
import com.eliteprompt.ai.PollinationsAiProvider;
import com.eliteprompt.ai.ZenAiProvider;
import com.eliteprompt.dao.UserDAO;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Runs the generated prompt through real AI and shows the answer.
 * Provider "pollinations" is free with no key; "zen" uses the
 * logged-in user's own saved API key (never hardcoded).
 */
@WebServlet("/run-ai")
public class RunAiServlet extends HttpServlet {

    // All available providers (order = UI dropdown order)
    private static final Map<String, AiProvider> PROVIDERS = new LinkedHashMap<>();
    static {
        PROVIDERS.put("pollinations", new PollinationsAiProvider());
        PROVIDERS.put("zen", new ZenAiProvider());
    }

    /** Used by the JSP to render the provider dropdown. */
    public static Map<String, AiProvider> getProviders() {
        return PROVIDERS;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isLoggedIn(req.getSession(false))) {
            resp.sendRedirect("login");
            return;
        }
        int userId = AuthUtil.currentUserId(req.getSession(false));

        // Keep the generator form values so the page re-shows them
        String title = req.getParameter("title");
        String category = req.getParameter("category");
        String requirement = req.getParameter("requirement");
        String tone = req.getParameter("tone");
        String outputFormat = req.getParameter("output_format");
        String generated = req.getParameter("generated_prompt");
        String providerId = req.getParameter("ai_provider");
        String model = req.getParameter("ai_model");

        AiProvider ai = PROVIDERS.getOrDefault(
                providerId == null ? "" : providerId, PROVIDERS.get("pollinations"));

        req.setAttribute("title", title == null ? "" : title);
        req.setAttribute("category", category == null ? "General" : category);
        req.setAttribute("requirement", requirement == null ? "" : requirement);
        req.setAttribute("tone", tone == null ? "Professional" : tone);
        req.setAttribute("output_format", outputFormat == null ? "Paragraph" : outputFormat);
        req.setAttribute("generated", generated == null ? "" : generated);
        req.setAttribute("ai_provider", ai.id());
        req.setAttribute("ai_model", model);
        // score/tips recomputed so the page looks identical
        if (requirement != null && !requirement.trim().isEmpty()) {
            req.setAttribute("score", com.eliteprompt.util.PromptGenerator.score(requirement));
            req.setAttribute("tips", com.eliteprompt.util.PromptGenerator.tips(requirement));
        }

        if (generated == null || generated.trim().isEmpty()) {
            req.setAttribute("aiError", "Generate a prompt first, then run it with AI.");
            req.getRequestDispatcher("/prompt_generator.jsp").forward(req, resp);
            return;
        }

        try {
            String apiKey = "";
            if (ai.needsApiKey()) {
                apiKey = new UserDAO().getZenApiKey(userId);
            }
            String answer = ai.complete(apiKey, model, generated.trim());
            req.setAttribute("aiAnswer", answer);
        } catch (Exception e) {
            req.setAttribute("aiError", e.getMessage());
        }
        req.getRequestDispatcher("/prompt_generator.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("generator");
    }
}
