<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Settings - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    Boolean hasKey = (Boolean) request.getAttribute("hasKey");
    String keyHint = (String) request.getAttribute("keyHint");
    if (hasKey == null) hasKey = false;
    if (keyHint == null) keyHint = "";
%>
<div class="container py-5">
    <div class="auth-card glass-card" style="max-width:650px;">
        <div class="text-center mb-4">
            <span class="brand-icon"><i class="fas fa-key"></i></span>
            <h3 class="mt-3">AI Settings</h3>
            <p class="text-muted">Connect your own OpenCode Zen API key to run prompts with real AI.</p>
        </div>
        <% if (error != null) { %><div class="alert alert-danger"><%= error %></div><% } %>
        <% if (success != null) { %><div class="alert alert-success"><%= success %></div><% } %>

        <div class="alert alert-info">
            <strong>How to get a key:</strong>
            <ol class="mb-0 mt-1">
                <li>Sign in at <a href="https://opencode.ai" target="_blank" rel="noopener">opencode.ai</a></li>
                <li>Add billing details / credits and copy your API key</li>
                <li>Paste it below and save (charged per request by Zen)</li>
            </ol>
        </div>

        <p>Current status:
            <% if (hasKey) { %><span class="badge bg-success">Key saved (<%= keyHint %>)</span>
            <% } else { %><span class="badge bg-secondary">No key saved</span><% } %>
        </p>

        <form method="POST" action="${pageContext.request.contextPath}/settings">
            <div class="mb-3">
                <label class="form-label">Zen API Key</label>
                <input type="password" name="zen_api_key" class="form-control" placeholder="Paste new key here" autocomplete="off">
                <small class="text-muted">Stored in your account only. Never shown fully, never hardcoded.</small>
            </div>
            <button type="submit" class="btn btn-primary w-100"><i class="fas fa-save me-1"></i> Save Key</button>
        </form>

        <% if (hasKey) { %>
        <form method="POST" action="${pageContext.request.contextPath}/settings" class="mt-2">
            <input type="hidden" name="clear_key" value="1">
            <button type="submit" class="btn btn-outline-danger w-100" onclick="return confirm('Remove your saved API key?')">Remove Key</button>
        </form>
        <% } %>

        <div class="text-center mt-3"><a href="${pageContext.request.contextPath}/generator">Back to Generator</a></div>
    </div>
</div>
<jsp:include page="includes/footer.jsp" />
