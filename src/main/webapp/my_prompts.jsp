<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.eliteprompt.model.Prompt, com.eliteprompt.util.PromptGenerator" %>
<% request.setAttribute("pageTitle", "My Prompts - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    List<Prompt> prompts = (List<Prompt>) request.getAttribute("prompts");
    String search = (String) request.getAttribute("search");
    String filter = (String) request.getAttribute("filter");
    String tag = (String) request.getAttribute("tag");
    Boolean favOnly = (Boolean) request.getAttribute("favOnly");
    Integer currentPage = (Integer) request.getAttribute("page");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer total = (Integer) request.getAttribute("total");
    String flashSuccess = (String) request.getAttribute("flashSuccess");
    String flashError = (String) request.getAttribute("flashError");
    if (search == null) search = "";
    if (filter == null) filter = "";
    if (tag == null) tag = "";
    if (favOnly == null) favOnly = false;
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (total == null) total = 0;
    if (prompts == null) prompts = new java.util.ArrayList<>();
    String ctx = request.getContextPath();
    // keep filters in pagination links
    String pageQuery = "search=" + java.net.URLEncoder.encode(search, "UTF-8")
        + "&category=" + java.net.URLEncoder.encode(filter, "UTF-8")
        + "&tag=" + java.net.URLEncoder.encode(tag, "UTF-8")
        + (favOnly ? "&fav=1" : "");
%>
<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
        <div><h2>My <span class="gradient-text">Prompts</span></h2><p class="text-muted mb-0"><%= total %> prompt(s) found</p></div>
        <div class="d-flex gap-2">
            <a href="<%=ctx%>/export" class="btn btn-outline-light"><i class="fas fa-download me-1"></i> Export JSON</a>
            <a href="<%=ctx%>/generator" class="btn btn-primary"><i class="fas fa-plus me-1"></i> New Prompt</a>
        </div>
    </div>
    <% if (flashSuccess != null) { %><div class="alert alert-success"><%= flashSuccess %></div><% } %>
    <% if (flashError != null) { %><div class="alert alert-danger"><%= flashError %></div><% } %>
    <div class="glass-card mb-4">
        <form method="GET" action="<%=ctx%>/my-prompts" class="row g-3">
            <div class="col-md-4"><input type="text" name="search" id="searchInput" class="form-control" placeholder="Search..." value="<%= search %>"></div>
            <div class="col-md-3">
                <select name="category" id="filterCategory" class="form-select">
                    <option value="">All Categories</option>
                    <% for (String c : PromptGenerator.getCategories()) { %>
                        <option value="<%= c %>" <%= filter.equals(c) ? "selected" : "" %>><%= c %></option>
                    <% } %>
                </select>
            </div>
            <div class="col-md-2"><input type="text" name="tag" class="form-control" placeholder="Tag..." value="<%= tag %>"></div>
            <div class="col-md-1 d-flex align-items-center">
                <div class="form-check"><input type="checkbox" name="fav" value="1" class="form-check-input" id="favCheck" <%= favOnly ? "checked" : "" %>><label class="form-check-label text-light" for="favCheck" title="Favorites only"><i class="fas fa-star text-warning"></i></label></div>
            </div>
            <div class="col-md-2"><button type="submit" class="btn btn-primary w-100">Search</button></div>
        </form>
    </div>
    <% if (prompts.isEmpty()) { %>
        <div class="glass-card text-center"><h5>No prompts found</h5><p class="text-muted">Create a new prompt.</p><a href="<%=ctx%>/generator" class="btn btn-primary">Create Prompt</a></div>
    <% } else { %>
        <div class="row g-4">
        <% for (Prompt p : prompts) { %>
            <div class="col-md-6 col-lg-4 prompt-item" data-category="<%= p.getCategory() %>">
                <div class="glass-card h-100 d-flex flex-column">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <h6 class="mb-0"><%= p.getTitle() %>
                            <% if (p.isFavorite()) { %><i class="fas fa-star text-warning ms-1" title="Favorite"></i><% } %>
                            <% if (p.isPublic()) { %><i class="fas fa-globe text-info ms-1" title="Public"></i><% } %>
                        </h6>
                        <span class="badge badge-category"><%= p.getCategory() %></span>
                    </div>
                    <% if (p.getTags() != null && !p.getTags().isEmpty()) { %>
                        <div class="mb-2"><% for (String t : p.getTags().split(",")) { %><span class="badge bg-secondary me-1">#<%= t.trim() %></span><% } %></div>
                    <% } %>
                    <div class="prompt-output small mb-2" style="min-height:80px;"><%= p.getGeneratedPrompt().length() > 150 ? p.getGeneratedPrompt().substring(0,150)+"..." : p.getGeneratedPrompt() %></div>
                    <small class="text-muted mb-3">Tone: <%= p.getTone() %> | <%= p.getOutputFormat() %></small>
                    <div class="d-flex gap-2 mt-auto flex-wrap">
                        <button class="btn btn-sm btn-outline-light" data-bs-toggle="modal" data-bs-target="#view<%= p.getId() %>">View</button>
                        <a href="<%=ctx%>/toggle-favorite?id=<%= p.getId() %>" class="btn btn-sm <%= p.isFavorite() ? "btn-warning" : "btn-outline-warning" %>" title="Toggle favorite"><i class="fas fa-star"></i></a>
                        <a href="<%=ctx%>/toggle-public?id=<%= p.getId() %>" class="btn btn-sm <%= p.isPublic() ? "btn-info" : "btn-outline-info" %>" title="Toggle public"><i class="fas fa-globe"></i></a>
                        <a href="<%=ctx%>/edit-prompt?id=<%= p.getId() %>" class="btn btn-sm btn-outline-warning"><i class="fas fa-edit"></i> Edit</a>
                        <a href="<%=ctx%>/delete-prompt?id=<%= p.getId() %>" class="btn btn-sm btn-outline-danger" onclick="return confirmDelete()"><i class="fas fa-trash"></i> Delete</a>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="view<%= p.getId() %>" tabindex="-1"><div class="modal-dialog modal-lg"><div class="modal-content bg-dark text-light">
                <div class="modal-header"><h5><%= p.getTitle() %></h5><button class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div>
                <div class="modal-body"><p><span class="badge badge-category"><%= p.getCategory() %></span></p><div class="prompt-output" id="promptText<%= p.getId() %>"><%= p.getGeneratedPrompt() %></div></div>
                <div class="modal-footer"><button class="btn btn-outline-light" onclick="copyTextById('promptText<%= p.getId() %>')">Copy</button><a href="<%=ctx%>/edit-prompt?id=<%= p.getId() %>" class="btn btn-primary">Edit</a></div>
            </div></div></div>
        <% } %>
        </div>
        <% if (totalPages > 1) { %>
        <nav class="mt-4"><ul class="pagination justify-content-center">
            <li class="page-item <%= currentPage <= 1 ? "disabled" : "" %>"><a class="page-link" href="<%=ctx%>/my-prompts?<%= pageQuery %>&page=<%= currentPage - 1 %>">Prev</a></li>
            <% for (int i = 1; i <= totalPages; i++) { %>
                <li class="page-item <%= i == currentPage ? "active" : "" %>"><a class="page-link" href="<%=ctx%>/my-prompts?<%= pageQuery %>&page=<%= i %>"><%= i %></a></li>
            <% } %>
            <li class="page-item <%= currentPage >= totalPages ? "disabled" : "" %>"><a class="page-link" href="<%=ctx%>/my-prompts?<%= pageQuery %>&page=<%= currentPage + 1 %>">Next</a></li>
        </ul></nav>
        <% } %>
    <% } %>
</div>
<jsp:include page="includes/footer.jsp" />
