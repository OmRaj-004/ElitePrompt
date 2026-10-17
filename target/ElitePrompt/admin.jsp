<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.eliteprompt.model.User, com.eliteprompt.model.Admin" %>
<% request.setAttribute("pageTitle", "Admin Dashboard - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    List<User> users = (List<User>) request.getAttribute("users");
    List<Admin> admins = (List<Admin>) request.getAttribute("admins");
    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
    Integer adminCount = (Integer) request.getAttribute("adminCount");
    String flashSuccess = (String) request.getAttribute("adminFlashSuccess");
    String flashError = (String) request.getAttribute("adminFlashError");
    if (users == null) users = new java.util.ArrayList<>();
    if (admins == null) admins = new java.util.ArrayList<>();
    if (totalUsers == null) totalUsers = 0;
    if (adminCount == null) adminCount = 0;
    String myUsername = (String) session.getAttribute("username");
%>
<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
        <div>
            <h2><i class="fas fa-shield-alt me-2"></i>Admin <span class="gradient-text">Dashboard</span></h2>
            <p class="text-muted mb-0">Monitor users and admins from one place.</p>
        </div>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-light"><i class="fas fa-arrow-left me-1"></i> Back to Dashboard</a>
    </div>

    <% if (flashSuccess != null) { %><div class="alert alert-success"><%= flashSuccess %></div><% } %>
    <% if (flashError != null) { %><div class="alert alert-danger"><%= flashError %></div><% } %>

    <!-- Stats -->
    <div class="row g-4 mb-4">
        <div class="col-md-6"><div class="glass-card stat-card"><div class="stat-number gradient-text"><%= totalUsers %></div><div class="stat-label"><i class="fas fa-users me-1"></i> Users</div></div></div>
        <div class="col-md-6"><div class="glass-card stat-card"><div class="stat-number gradient-text"><%= adminCount %></div><div class="stat-label"><i class="fas fa-user-shield me-1"></i> Admins</div></div></div>
    </div>

    <!-- Users table -->
    <div class="glass-card mb-4">
        <h5 class="mb-3"><i class="fas fa-users me-2"></i>Users</h5>
        <div class="table-responsive">
            <table class="table table-dark table-striped table-hover align-middle">
                <thead>
                    <tr>
                        <th>ID</th><th>Name</th><th>Username</th><th>Email</th>
                        <th>Prompts</th><th>Joined</th><th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (User u : users) { %>
                    <tr>
                        <td><%= u.getId() %></td>
                        <td><%= u.getFullName() %></td>
                        <td><%= u.getUsername() %></td>
                        <td><%= u.getEmail() %></td>
                        <td><span class="badge bg-primary"><%= u.getPromptCount() %></span></td>
                        <td><small class="text-muted"><%= u.getCreatedAt() %></small></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin-action?id=<%= u.getId() %>&action=react" class="btn btn-sm btn-outline-warning" title="Promote to admin"><i class="fas fa-user-plus"></i></a>
                            <a href="${pageContext.request.contextPath}/admin-action?id=<%= u.getId() %>&action=remove" class="btn btn-sm btn-outline-danger" onclick="return confirm('Remove user <%= u.getUsername() %>? All their prompts will be deleted.')"><i class="fas fa-trash"></i></a>
                        </td>
                    </tr>
                    <% } %>
                    <% if (users.isEmpty()) { %><tr><td colspan="7" class="text-center text-muted">No users yet.</td></tr><% } %>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Admins table (separate table for security) -->
    <div class="glass-card">
        <h5 class="mb-3"><i class="fas fa-user-shield me-2"></i>Admins <span class="badge bg-secondary ms-1">separate table</span></h5>
        <div class="table-responsive">
            <table class="table table-dark table-striped table-hover align-middle">
                <thead>
                    <tr>
                        <th>ID</th><th>Name</th><th>Username</th><th>Email</th>
                        <th>Joined</th><th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Admin a : admins) { %>
                    <tr>
                        <td><%= a.getId() %></td>
                        <td><%= a.getFullName() %></td>
                        <td><%= a.getUsername() %></td>
                        <td><%= a.getEmail() %></td>
                        <td><small class="text-muted"><%= a.getCreatedAt() %></small></td>
                        <td>
                            <% if (!a.getUsername().equals(myUsername)) { %>
                                <a href="${pageContext.request.contextPath}/admin-action?id=<%= a.getId() %>&action=react" class="btn btn-sm btn-outline-secondary" title="Demote to user"><i class="fas fa-user-minus"></i></a>
                                <a href="${pageContext.request.contextPath}/admin-action?id=<%= a.getId() %>&action=remove" class="btn btn-sm btn-outline-danger" onclick="return confirm('Remove admin <%= a.getUsername() %>?')"><i class="fas fa-trash"></i></a>
                            <% } else { %>
                                <span class="badge bg-info">You</span>
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                    <% if (admins.isEmpty()) { %><tr><td colspan="6" class="text-center text-muted">No admins yet.</td></tr><% } %>
                </tbody>
            </table>
        </div>
        <small class="text-muted">Admins are stored in a separate <code>admins</code> table - never in <code>users</code>. React = promote/demote. You cannot change your own account.</small>
    </div>
</div>
<jsp:include page="includes/footer.jsp" />
