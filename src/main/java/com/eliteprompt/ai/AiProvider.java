package com.eliteprompt.ai;

/**
 * AiProvider - one interface for any LLM vendor.
 * Lets the app swap vendors (Zen, Ollama, Gemini...) without touching servlets.
 * Easy viva explanation: strategy pattern.
 */
public interface AiProvider {

    /** Short id used in forms, e.g. "zen" or "pollinations". */
    String id();

    /** Display name for the UI dropdown. */
    String displayName();

    /** True when the user must save an API key first (Zen). */
    boolean needsApiKey();

    /**
     * Send a prompt to the AI and return its text answer.
     * @param apiKey  the user's own key (ignored by keyless providers)
     * @param model   vendor model id, e.g. "qwen3.8-max"
     * @param prompt  the full generated prompt to execute
     * @return the AI's answer text
     * @throws Exception with a readable message when the call fails
     */
    String complete(String apiKey, String model, String prompt) throws Exception;

    /** Models offered in the UI dropdown. */
    String[] supportedModels();
}
