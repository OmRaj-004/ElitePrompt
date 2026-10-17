<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.eliteprompt.util.PromptGenerator" %>
<% request.setAttribute("pageTitle", "Generate Prompt - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    String[] categories = PromptGenerator.getCategories();
    String[] tones = PromptGenerator.getTones();
    String[] formats = PromptGenerator.getFormats();
    String title = (String) request.getAttribute("title");
    String category = (String) request.getAttribute("category");
    String requirement = (String) request.getAttribute("requirement");
    String tone = (String) request.getAttribute("tone");
    String output_format = (String) request.getAttribute("output_format");
    String generated = (String) request.getAttribute("generated");
    Integer score = (Integer) request.getAttribute("score");
    List<String> tips = (List<String>) request.getAttribute("tips");
    String aiAnswer = (String) request.getAttribute("aiAnswer");
    String aiError = (String) request.getAttribute("aiError");
    String aiProvider = (String) request.getAttribute("ai_provider");
    java.util.Map<String, com.eliteprompt.ai.AiProvider> aiProviders =
            com.eliteprompt.servlet.RunAiServlet.getProviders();
    if (aiProvider == null || !aiProviders.containsKey(aiProvider)) aiProvider = "pollinations";
    String aiModel = (String) request.getAttribute("ai_model");
    if (aiModel == null) aiModel = "";
    if (title == null) title = "";
    if (category == null) category = "General";
    if (requirement == null) requirement = "";
    if (tone == null) tone = "Professional";
    if (output_format == null) output_format = "Paragraph";
    if (generated == null) generated = "";
    if (score == null) score = 0;
    if (tips == null) tips = new java.util.ArrayList<>();
    if (aiAnswer == null) aiAnswer = "";
    if (aiError == null) aiError = "";
    if (aiModel == null || aiModel.isEmpty()) aiModel = "openai";
    String ctx = request.getContextPath();
%>
<div class="container py-5">
    <div class="text-center mb-4">
        <h2><i class="fas fa-magic me-2"></i>Prompt <span class="gradient-text">Generator</span></h2>
        <p class="text-muted">Fill the form and click Generate. Then copy or save.</p>
    </div>
    <div class="row g-4">
        <div class="col-lg-6"><div class="glass-card">
            <h5 class="mb-3">Your Requirement</h5>
            <form method="POST" action="<%=ctx%>/generator" id="generatorForm">
                <div class="mb-3"><label class="form-label">Prompt Title</label><input type="text" name="title" id="title" class="form-control" value="<%= title %>" required></div>
                <div class="mb-3"><label class="form-label">Category</label>
                    <select name="category" id="category" class="form-select">
                        <% for (String c : categories) { %><option value="<%= c %>" <%= category.equals(c) ? "selected" : "" %>><%= c %></option><% } %>
                    </select>
                </div>
                <div class="mb-3"><label class="form-label">User Requirement</label><textarea name="requirement" id="requirement" class="form-control" rows="4" required><%= requirement %></textarea></div>
                <div class="row">
                    <div class="col-md-6 mb-3"><label class="form-label">Tone / Style</label>
                        <select name="tone" id="tone" class="form-select"><% for (String t : tones) { %><option <%= tone.equals(t) ? "selected" : "" %>><%= t %></option><% } %></select>
                    </div>
                    <div class="col-md-6 mb-3"><label class="form-label">Output Format</label>
                        <select name="output_format" id="output_format" class="form-select"><% for (String f : formats) { %><option <%= output_format.equals(f) ? "selected" : "" %>><%= f %></option><% } %></select>
                    </div>
                </div>
                <button type="button" id="generateBtn" class="btn btn-primary w-100 mb-2"><i class="fas fa-wand-magic-sparkles me-1"></i> Generate (JavaScript)</button>
                <button type="submit" class="btn btn-outline-light w-100 btn-sm">Generate via Server (Java)</button>
            </form>
        </div></div>
        <div class="col-lg-6"><div class="glass-card">
            <h5 class="mb-3">Generated Prompt</h5>
            <div id="resultSection" style="<%= generated.isEmpty() ? "display:none;" : "" %>">
                <div class="prompt-output mb-3" id="generatedPrompt"><%= generated %></div>
                <div class="mb-3"><span class="badge <%= score >= 80 ? "bg-success" : (score >= 60 ? "bg-warning text-dark" : "bg-danger") %>">Quality Score: <%= score %>/100</span></div>
                <% if (!tips.isEmpty()) { %>
                <div class="alert alert-info py-2"><small><strong>Tips:</strong><ul class="mb-0"><% for (String tip : tips) { %><li><%= tip %></li><% } %></ul></small></div>
                <% } %>
                <div class="d-flex gap-2 flex-wrap"><button class="btn btn-outline-light" onclick="copyPrompt()"><i class="fas fa-copy me-1"></i> Copy</button><button class="btn btn-outline-light" onclick="showLiveScore()"><i class="fas fa-gauge me-1"></i> Live Score</button></div>
                <div id="liveScore" class="mt-2"></div>
                <hr class="border-secondary">
                <h6><i class="fas fa-robot me-1"></i>Run with real AI (OpenCode Zen)</h6>
                <% if (!aiError.isEmpty()) { %><div class="alert alert-danger"><%= aiError %> <a href="<%=ctx%>/settings" class="alert-link">Open Settings</a></div><% } %>
                <% if (!aiAnswer.isEmpty()) { %><div class="prompt-output mb-2 ai-answer" id="aiAnswer" data-markdown><%= aiAnswer %></div><button class="btn btn-sm btn-outline-light mb-2" onclick="copyTextById('aiAnswer')"><i class="fas fa-copy me-1"></i> Copy Answer</button><% } %>
                <form method="POST" action="<%=ctx%>/run-ai">
                    <input type="hidden" name="title" id="ai_title">
                    <input type="hidden" name="category" id="ai_category">
                    <input type="hidden" name="requirement" id="ai_requirement">
                    <input type="hidden" name="tone" id="ai_tone">
                    <input type="hidden" name="output_format" id="ai_format">
                    <input type="hidden" name="generated_prompt" id="ai_generated" value="<%= generated %>">
                    <div class="row g-2 align-items-end">
                        <div class="col-md-5"><label class="form-label">AI Provider</label>
                            <select name="ai_provider" id="ai_provider" class="form-select" onchange="swapAiModels()">
                                <% for (java.util.Map.Entry<String, com.eliteprompt.ai.AiProvider> e : aiProviders.entrySet()) { %><option value="<%= e.getKey() %>" <%= aiProvider.equals(e.getKey()) ? "selected" : "" %>><%= e.getValue().displayName() %></option><% } %>
                            </select>
                        </div>
                        <div class="col-md-4"><label class="form-label">AI Model</label>
                            <select name="ai_model" id="ai_model" class="form-select"></select>
                        </div>
                        <div class="col-md-3"><button type="submit" class="btn btn-primary w-100" onclick="syncAiFields(event)"><i class="fas fa-play me-1"></i> Run with AI</button></div>
                    </div>
                    <small class="text-muted">Pollinations is free with no key. Zen needs your key (<a href="<%=ctx%>/settings">Settings</a>).</small>
                </form>
                <hr class="border-secondary">
                <h6>Save this prompt</h6>
                <form method="POST" action="<%=ctx%>/save-prompt">
                    <input type="hidden" name="title" id="save_title">
                    <input type="hidden" name="category" id="save_category">
                    <input type="hidden" name="requirement" id="save_requirement">
                    <input type="hidden" name="tone" id="save_tone">
                    <input type="hidden" name="output_format" id="save_format">
                    <input type="hidden" name="generated_prompt" id="generated_prompt_input" value="<%= generated %>">
                    <div class="mb-2"><label class="form-label">Tags (comma separated)</label><input type="text" name="tags" id="save_tags" class="form-control" placeholder="e.g. instagram, fitness, social"></div>
                    <div class="form-check mb-2"><input type="checkbox" name="is_public" value="1" class="form-check-input" id="save_public"><label class="form-check-label text-light" for="save_public">Share in public Gallery</label></div>
                    <button type="submit" class="btn btn-primary w-100 mt-2" onclick="syncSaveFields(event)"><i class="fas fa-save me-1"></i> Save to My Prompts</button>
                </form>
            </div>
            <div id="emptyResult" style="<%= generated.isEmpty() ? "" : "display:none;" %>">
                <p class="text-muted">Your generated prompt will appear here.</p>
            </div>
        </div></div>
    </div>
</div>
<script>
// Model lists come from the server providers (single source of truth)
const AI_MODELS = {
<% for (java.util.Map.Entry<String, com.eliteprompt.ai.AiProvider> pe : aiProviders.entrySet()) { %>
    "<%= pe.getKey() %>": [<% String[] ms = pe.getValue().supportedModels(); for (int mi = 0; mi < ms.length; mi++) { %>"<%= ms[mi] %>"<%= mi < ms.length - 1 ? "," : "" %><% } %>],
<% } %>
};
const AI_SELECTED_MODEL = "<%= aiModel %>";
function swapAiModels() {
    const prov = document.getElementById('ai_provider').value;
    const sel = document.getElementById('ai_model');
    sel.innerHTML = "";
    (AI_MODELS[prov] || []).forEach(m => {
        const o = document.createElement('option');
        o.value = m; o.textContent = m;
        if (m === AI_SELECTED_MODEL) o.selected = true;
        sel.appendChild(o);
    });
}
document.addEventListener('DOMContentLoaded', swapAiModels);
function syncSaveFields(e) {
    document.getElementById('save_title').value = document.getElementById('title').value;
    document.getElementById('save_category').value = document.getElementById('category').value;
    document.getElementById('save_requirement').value = document.getElementById('requirement').value;
    document.getElementById('save_tone').value = document.getElementById('tone').value;
    document.getElementById('save_format').value = document.getElementById('output_format').value;
    if (!document.getElementById('generated_prompt_input').value.trim()) {
        e.preventDefault();
        alert('Please generate a prompt first.');
    }
}
// Copy the same form values for the Run-with-AI request
function syncAiFields(e) {
    document.getElementById('ai_title').value = document.getElementById('title').value;
    document.getElementById('ai_category').value = document.getElementById('category').value;
    document.getElementById('ai_requirement').value = document.getElementById('requirement').value;
    document.getElementById('ai_tone').value = document.getElementById('tone').value;
    document.getElementById('ai_format').value = document.getElementById('output_format').value;
    if (!document.getElementById('ai_generated').value.trim()) {
        e.preventDefault();
        alert('Please generate a prompt first.');
    }
}
</script>
<jsp:include page="includes/footer.jsp" />
