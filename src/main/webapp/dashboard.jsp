<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.eliteprompt.model.Prompt" %>
<% request.setAttribute("pageTitle", "Dashboard - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    String fullName = (String) session.getAttribute("fullName");
    if (fullName == null) fullName = (String) session.getAttribute("username");
    Integer total = (Integer) request.getAttribute("total");
    if (total == null) total = 0;
    Integer favCount = (Integer) request.getAttribute("favCount");
    if (favCount == null) favCount = 0;
    List<Prompt> recent = (List<Prompt>) request.getAttribute("recent");
    String ctx = request.getContextPath();
%>
<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
        <div>
            <h2>Welcome, <span class="gradient-text"><%= fullName %></span></h2>
            <p class="text-muted mb-0">Manage your AI prompts from one place.</p>
        </div>
        <a href="<%=ctx%>/generator" class="btn btn-primary"><i class="fas fa-magic me-1"></i> Quick Generate</a>
    </div>
    <div class="row g-4 mb-4">
        <div class="col-md-4"><div class="glass-card stat-card"><div class="stat-number gradient-text"><%= total %></div><div class="stat-label"><i class="fas fa-folder me-1"></i> Total Prompts</div></div></div>
        <div class="col-md-4"><div class="glass-card stat-card"><div class="stat-number gradient-text"><%= favCount %></div><div class="stat-label"><i class="fas fa-star me-1"></i> Favorites</div></div></div>
        <div class="col-md-4"><div class="glass-card stat-card"><div class="stat-number gradient-text"><%= recent != null ? recent.size() : 0 %></div><div class="stat-label"><i class="fas fa-clock me-1"></i> Recent Shown</div></div></div>
    </div>
    <div class="row g-4">
        <div class="col-lg-8"><div class="glass-card">
            <div class="d-flex justify-content-between align-items-center mb-3"><h5 class="mb-0"><i class="fas fa-history me-2"></i>Recent Prompts</h5><a href="<%=ctx%>/my-prompts" class="btn btn-sm btn-outline-light">View All</a></div>
            <% if (recent == null || recent.isEmpty()) { %>
                <p class="text-muted">No prompts yet. <a href="<%=ctx%>/generator">Create your first prompt</a>.</p>
            <% } else { %>
                <div class="list-group">
                <% for (Prompt p : recent) { %>
                    <div class="list-group-item bg-transparent text-light border-secondary mb-2 rounded">
                        <div class="d-flex justify-content-between"><strong><%= p.getTitle() %></strong><span class="badge badge-category"><%= p.getCategory() %></span></div>
                        <small class="text-muted"><%= p.getGeneratedPrompt().length() > 100 ? p.getGeneratedPrompt().substring(0,100)+"..." : p.getGeneratedPrompt() %></small>
                    </div>
                <% } %>
                </div>
            <% } %>
        </div></div>
        <div class="col-lg-4"><div class="glass-card">
            <h5><i class="fas fa-bolt me-2"></i>Quick Actions</h5>
            <div class="d-grid gap-2 mt-3">
                <a href="<%=ctx%>/generator" class="btn btn-primary">Generate Prompt</a>
                <a href="<%=ctx%>/my-prompts" class="btn btn-outline-light">My Prompts</a>
                <a href="<%=ctx%>/feedback" class="btn btn-outline-light">Give Feedback</a>
                <a href="<%=ctx%>/logout" class="btn btn-outline-danger">Logout</a>
            </div>
        </div></div>
    </div>
</div>
<jsp:include page="includes/footer.jsp" />
