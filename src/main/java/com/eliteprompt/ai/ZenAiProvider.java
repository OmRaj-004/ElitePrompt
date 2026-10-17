package com.eliteprompt.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ZenAiProvider - calls OpenCode Zen's OpenAI-compatible endpoint:
 *   POST https://opencode.ai/zen/v1/chat/completions
 *   Header: Authorization: Bearer <user's own Zen API key>
 *
 * Docs: https://opencode.ai/docs/zen#endpoints
 */
public class ZenAiProvider implements AiProvider {

    private static final String ENDPOINT = "https://opencode.ai/zen/v1/chat/completions";

    // Curated Zen models on the chat/completions endpoint (verified Oct 2026)
    private static final String[] MODELS = {
        "qwen3.8-max",
        "deepseek-v4-flash",
        "nemotron-3.5-lightning-free"
    };

    @Override
    public String id() { return "zen"; }

    @Override
    public String displayName() { return "OpenCode Zen (needs your API key)"; }

    @Override
    public boolean needsApiKey() { return true; }

    @Override
    public String[] supportedModels() {
        return MODELS.clone();
    }

    @Override
    public String complete(String apiKey, String model, String prompt) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("No Zen API key saved. Add yours on the Settings page first.");
        }
        if (model == null || model.trim().isEmpty()) {
            model = MODELS[0];
        }

        // Build OpenAI-style chat request body
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);

        JSONObject body = new JSONObject();
        body.put("model", model);
        body.put("messages", new JSONArray().put(message));
        body.put("max_tokens", 800);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .timeout(Duration.ofSeconds(90))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey.trim())
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception("Could not reach OpenCode Zen. Check your internet connection.");
        }

        if (response.statusCode() == 401 || response.statusCode() == 403) {
            throw new Exception("Zen rejected the API key (HTTP " + response.statusCode() + "). Check the key on the Settings page.");
        }
        if (response.statusCode() != 200) {
            throw new Exception("Zen returned HTTP " + response.statusCode() + ": " + shortBody(response.body()));
        }

        // OpenAI chat format: choices[0].message.content
        try {
            JSONObject json = new JSONObject(response.body());
            String answer = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            if (answer == null || answer.trim().isEmpty()) {
                throw new Exception("Zen returned an empty answer.");
            }
            return answer.trim();
        } catch (Exception e) {
            throw new Exception("Could not understand Zen's reply: " + shortBody(response.body()));
        }
    }

    private String shortBody(String body) {
        if (body == null) return "";
        return body.length() > 300 ? body.substring(0, 300) + "..." : body;
    }
}
