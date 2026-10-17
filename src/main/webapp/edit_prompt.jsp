<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.eliteprompt.model.Prompt, com.eliteprompt.util.PromptGenerator" %>
<% request.setAttribute("pageTitle", "Edit Prompt - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    Prompt p = (Prompt) request.getAttribute("prompt");
    String error = (String) request.getAttribute("error");
    String ctx = request.getContextPath();
%>
<div class="container py-5">
    <div class="auth-card glass-card" style="max-width:700px;">
        <h3><i class="fas fa-edit me-2"></i>Edit Prompt</h3>
        <% if (error != null) { %><div class="alert alert-danger"><%= error %></div><% } %>
        <% if (p != null) { %>
        <form method="POST" action="<%=ctx%>/edit-prompt?id=<%= p.getId() %>">
            <div class="mb-3"><label class="form-label">Title</label><input type="text" name="title" class="form-control" value="<%= p.getTitle() %>" required></div>
            <div class="mb-3"><label class="form-label">Category</label>
                <select name="category" class="form-select">
                <% for (String c : PromptGenerator.getCategories()) { %>
                    <option <%= p.getCategory().equals(c) ? "selected" : "" %>><%= c %></option>
                <% } %>
                </select>
            </div>
            <div class="mb-3"><label class="form-label">Requirement</label><textarea name="requirement" class="form-control" rows="3" required><%= p.getRequirement() %></textarea></div>
            <div class="row">
                <div class="col-md-6 mb-3"><label class="form-label">Tone</label><input type="text" name="tone" class="form-control" value="<%= p.getTone() %>" required></div>
                <div class="col-md-6 mb-3"><label class="form-label">Output Format</label><input type="text" name="output_format" class="form-control" value="<%= p.getOutputFormat() %>" required></div>
            </div>
            <div class="mb-3"><label class="form-label">Generated Prompt</label><textarea name="generated_prompt" class="form-control" rows="5" required><%= p.getGeneratedPrompt() %></textarea></div>
            <div class="mb-3"><label class="form-label">Tags (comma separated)</label><input type="text" name="tags" class="form-control" value="<%= p.getTags() == null ? "" : p.getTags() %>" placeholder="e.g. instagram, fitness"></div>
            <div class="form-check mb-3"><input type="checkbox" name="is_public" value="1" class="form-check-input" id="edit_public" <%= p.isPublic() ? "checked" : "" %>><label class="form-check-label text-light" for="edit_public">Share in public Gallery</label></div>
            <div class="d-flex gap-2"><button type="submit" class="btn btn-primary flex-fill">Update</button><a href="<%=ctx%>/my-prompts" class="btn btn-outline-light">Cancel</a></div>
        </form>
        <% } %>
    </div>
</div>
<jsp:include page="includes/footer.jsp" />
