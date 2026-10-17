package com.eliteprompt.ai;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * PollinationsAiProvider - free AI with NO api key:
 *   GET https://text.pollinations.ai/{prompt}?model=openai
 * Returns plain text. Verified live Oct 2026.
 * Docs: https://github.com/pollinations/pollinations
 */
public class PollinationsAiProvider implements AiProvider {

    private static final String BASE = "https://text.pollinations.ai/";

    // Only model names verified live - unverified names return "{}"
    private static final String[] MODELS = { "openai" };

    @Override
    public String id() { return "pollinations"; }

    @Override
    public String displayName() { return "Pollinations Free (no key needed)"; }

    @Override
    public boolean needsApiKey() { return false; }

    @Override
    public String[] supportedModels() {
        return MODELS.clone();
    }

    @Override
    public String complete(String apiKey, String model, String prompt) throws Exception {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new Exception("Nothing to send to the AI.");
        }
        if (model == null || model.trim().isEmpty()) {
            model = MODELS[0];
        }

        String url = BASE + URLEncoder.encode(prompt.trim(), StandardCharsets.UTF_8)
                + "?model=" + URLEncoder.encode(model.trim(), StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(120))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception("Could not reach Pollinations. Check your internet connection.");
        }

        if (response.statusCode() != 200) {
            throw new Exception("Pollinations returned HTTP " + response.statusCode() + ". Try again in a moment.");
        }
        String answer = response.body() == null ? "" : response.body().trim();
        if (answer.isEmpty() || answer.equals("{}")) {
            throw new Exception("Pollinations returned an empty answer. Try again.");
        }
        return answer;
    }
}
