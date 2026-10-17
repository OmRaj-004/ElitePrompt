<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.eliteprompt.model.Prompt, com.eliteprompt.util.PromptGenerator" %>
<% request.setAttribute("pageTitle", "Public Gallery - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    List<Prompt> prompts = (List<Prompt>) request.getAttribute("prompts");
    String search = (String) request.getAttribute("search");
    String filter = (String) request.getAttribute("filter");
    Integer currentPage = (Integer) request.getAttribute("page");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer total = (Integer) request.getAttribute("total");
    String flashSuccess = (String) request.getAttribute("flashSuccess");
    String flashError = (String) request.getAttribute("flashError");
    boolean loggedIn = session != null && session.getAttribute("userId") != null;
    if (search == null) search = "";
    if (filter == null) filter = "";
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (total == null) total = 0;
    if (prompts == null) prompts = new java.util.ArrayList<>();
    String ctx = request.getContextPath();
    String pageQuery = "search=" + java.net.URLEncoder.encode(search, "UTF-8")
        + "&category=" + java.net.URLEncoder.encode(filter, "UTF-8");
%>
<div class="container py-5">
    <div class="text-center mb-4">
        <h2><i class="fas fa-globe me-2"></i>Public <span class="gradient-text">Gallery</span></h2>
        <p class="text-muted">Prompts shared by the community. Clone any prompt into your library.</p>
    </div>
    <% if (flashSuccess != null) { %><div class="alert alert-success"><%= flashSuccess %></div><% } %>
    <% if (flashError != null) { %><div class="alert alert-danger"><%= flashError %></div><% } %>
    <div class="glass-card mb-4">
        <form method="GET" action="<%=ctx%>/gallery" class="row g-3">
            <div class="col-md-7"><input type="text" name="search" class="form-control" placeholder="Search public prompts..." value="<%= search %>"></div>
            <div class="col-md-3">
                <select name="category" class="form-select">
                    <option value="">All Categories</option>
                    <% for (String c : PromptGenerator.getCategories()) { %>
                        <option value="<%= c %>" <%= filter.equals(c) ? "selected" : "" %>><%= c %></option>
                    <% } %>
                </select>
            </div>
            <div class="col-md-2"><button type="submit" class="btn btn-primary w-100">Search</button></div>
        </form>
    </div>
    <% if (prompts.isEmpty()) { %>
        <div class="glass-card text-center"><h5>No public prompts yet</h5><p class="text-muted">Share one from My Prompts using the globe button.</p></div>
    <% } else { %>
        <p class="text-muted mb-3"><%= total %> shared prompt(s)</p>
        <div class="row g-4">
        <% for (Prompt p : prompts) { %>
            <div class="col-md-6 col-lg-4">
                <div class="glass-card h-100 d-flex flex-column">
                    <div class="d-flex justify-content-between mb-2"><h6 class="mb-0"><%= p.getTitle() %></h6><span class="badge badge-category"><%= p.getCategory() %></span></div>
                    <small class="text-muted mb-2"><i class="fas fa-user me-1"></i>by <%= p.getAuthorName() %></small>
                    <% if (p.getTags() != null && !p.getTags().isEmpty()) { %>
                        <div class="mb-2"><% for (String t : p.getTags().split(",")) { %><span class="badge bg-secondary me-1">#<%= t.trim() %></span><% } %></div>
                    <% } %>
                    <div class="prompt-output small mb-3" style="min-height:80px;"><%= p.getGeneratedPrompt().length() > 150 ? p.getGeneratedPrompt().substring(0,150)+"..." : p.getGeneratedPrompt() %></div>
                    <div class="d-flex gap-2 mt-auto">
                        <button class="btn btn-sm btn-outline-light" data-bs-toggle="modal" data-bs-target="#gview<%= p.getId() %>">View</button>
                        <% if (loggedIn) { %>
                            <a href="<%=ctx%>/clone-prompt?id=<%= p.getId() %>" class="btn btn-sm btn-primary"><i class="fas fa-clone me-1"></i> Clone</a>
                        <% } else { %>
                            <a href="<%=ctx%>/login" class="btn btn-sm btn-outline-light">Login to Clone</a>
                        <% } %>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="gview<%= p.getId() %>" tabindex="-1"><div class="modal-dialog modal-lg"><div class="modal-content bg-dark text-light">
                <div class="modal-header"><h5><%= p.getTitle() %></h5><button class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div>
                <div class="modal-body"><p><span class="badge badge-category"><%= p.getCategory() %></span> <small class="text-muted">by <%= p.getAuthorName() %></small></p><div class="prompt-output" id="gpromptText<%= p.getId() %>"><%= p.getGeneratedPrompt() %></div></div>
                <div class="modal-footer"><button class="btn btn-outline-light" onclick="copyTextById('gpromptText<%= p.getId() %>')">Copy</button></div>
            </div></div></div>
        <% } %>
        </div>
        <% if (totalPages > 1) { %>
        <nav class="mt-4"><ul class="pagination justify-content-center">
            <li class="page-item <%= currentPage <= 1 ? "disabled" : "" %>"><a class="page-link" href="<%=ctx%>/gallery?<%= pageQuery %>&page=<%= currentPage - 1 %>">Prev</a></li>
            <% for (int i = 1; i <= totalPages; i++) { %>
                <li class="page-item <%= i == currentPage ? "active" : "" %>"><a class="page-link" href="<%=ctx%>/gallery?<%= pageQuery %>&page=<%= i %>"><%= i %></a></li>
            <% } %>
            <li class="page-item <%= currentPage >= totalPages ? "disabled" : "" %>"><a class="page-link" href="<%=ctx%>/gallery?<%= pageQuery %>&page=<%= currentPage + 1 %>">Next</a></li>
        </ul></nav>
        <% } %>
    <% } %>
</div>
<jsp:include page="includes/footer.jsp" />
