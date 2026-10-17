package com.eliteprompt.util;

import java.util.HashMap;
import java.util.Map;

/**
 * PromptGenerator - builds structured prompts from templates.
 * Mirrors JS buildPrompt() so client and server generate identically.
 * No real AI API - easy to explain in viva.
 */
public class PromptGenerator {

    // Role template per category
    private static final Map<String, String> ROLES = new HashMap<>();

    static {
        ROLES.put("Education", "You are an expert tutor and educator.");
        ROLES.put("Business", "You are a senior business consultant with 15 years of experience.");
        ROLES.put("Content Creation", "You are an expert social media content creator.");
        ROLES.put("Programming", "You are a senior software developer and coding mentor.");
        ROLES.put("Marketing", "You are an expert digital marketer and copywriter.");
        ROLES.put("Resume & Career", "You are a professional career coach and resume expert.");
        ROLES.put("General", "You are a helpful AI assistant.");
    }

    public static String[] getCategories() {
        return new String[]{"Education", "Business", "Content Creation",
                "Programming", "Marketing", "Resume & Career", "General"};
    }

    public static String[] getTones() {
        return new String[]{"Professional", "Friendly", "Motivating",
                "Formal", "Creative", "Casual", "Persuasive"};
    }

    public static String[] getFormats() {
        return new String[]{"Paragraph", "Bullet Points", "Numbered List",
                "Table", "Short Caption", "Detailed Explanation", "Step-by-step Guide"};
    }

    /**
     * Build a structured prompt.
     * Example: "Create an Instagram caption..." ->
     * "You are an expert social media content creator. Create an Instagram caption... Use a motivating tone..."
     */
    public static String generate(String category, String requirement,
                                  String tone, String outputFormat) {
        String role = ROLES.getOrDefault(category, ROLES.get("General"));
        String req = requirement == null ? "" : requirement.trim();
        if (!req.endsWith(".")) {
            req += ".";
        }
        return role + " " + req
                + " Use a " + tone.toLowerCase() + " tone."
                + " Provide the output in " + outputFormat.toLowerCase() + " format."
                + " Keep it clear, concise and actionable.";
    }

    // Words that make a requirement specific and actionable
    private static final String[] ACTION_VERBS = {
        "create", "write", "explain", "generate", "design", "analyze",
        "summarize", "summarise", "list", "compare", "draft", "plan", "suggest"
    };

    /**
     * Heuristic quality score (0-100) for a requirement.
     * Simple checklist viva students can explain: length + action verb
     * + detail words + output hint.
     */
    public static int score(String requirement) {
        if (requirement == null) return 0;
        String req = requirement.trim().toLowerCase();
        if (req.isEmpty()) return 0;

        int points = 40; // base: user wrote something
        if (req.length() >= 20) points += 15;                       // enough detail
        if (req.split("\\s+").length >= 8) points += 15;           // enough words
        for (String verb : ACTION_VERBS) {                         // clear action
            if (req.contains(verb)) { points += 10; break; }
        }
        if (req.contains("for ") || req.contains("with ")          // context/audience
                || req.contains("audience") || req.contains("students")
                || req.contains("customers")) points += 10;
        if (req.contains("list") || req.contains("table")          // output hint
                || req.contains("steps") || req.contains("example")
                || req.contains("points")) points += 10;
        return Math.min(points, 100);
    }

    /** Short improvement tips for requirements scoring below 100. */
    public static java.util.List<String> tips(String requirement) {
        java.util.List<String> tips = new java.util.ArrayList<>();
        if (requirement == null || requirement.trim().isEmpty()) {
            tips.add("Describe what you want the AI to do.");
            return tips;
        }
        String req = requirement.trim().toLowerCase();
        if (req.length() < 20) tips.add("Add more detail (aim for at least 20 characters).");
        if (req.split("\\s+").length < 8) tips.add("Use at least 8 words so the AI has context.");
        boolean hasVerb = false;
        for (String verb : ACTION_VERBS) {
            if (req.contains(verb)) { hasVerb = true; break; }
        }
        if (!hasVerb) tips.add("Start with an action word like create, write, explain or plan.");
        if (!(req.contains("for ") || req.contains("audience")))
            tips.add("Mention who it is for (e.g. students, customers).");
        return tips;
    }
}
