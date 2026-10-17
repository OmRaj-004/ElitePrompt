<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Register - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    String full_name = (String) request.getAttribute("full_name");
    String username = (String) request.getAttribute("username");
    String email = (String) request.getAttribute("email");
    if (full_name == null) full_name = "";
    if (username == null) username = "";
    if (email == null) email = "";
%>
<div class="container">
    <div class="auth-card glass-card">
        <div class="text-center mb-4">
            <span class="brand-icon"><i class="fas fa-user-plus"></i></span>
            <h3 class="mt-3">Create Account</h3>
            <p class="text-muted">Join Elite Prompt today</p>
        </div>
        <% if (error != null) { %><div class="alert alert-danger"><%= error %></div><% } %>
        <% if (success != null) { %><div class="alert alert-success"><%= success %> <a href="${pageContext.request.contextPath}/login">Login now</a>.</div><% } %>
        <form method="POST" action="${pageContext.request.contextPath}/register">
            <div class="mb-3"><label class="form-label">Full Name</label><input type="text" name="full_name" class="form-control" value="<%= full_name %>" required></div>
            <div class="mb-3"><label class="form-label">Username</label><input type="text" name="username" class="form-control" value="<%= username %>" required></div>
            <div class="mb-3"><label class="form-label">Email</label><input type="email" name="email" class="form-control" value="<%= email %>" required></div>
            <div class="mb-3"><label class="form-label">Password</label><input type="password" name="password" class="form-control" placeholder="Min. 6 characters" required></div>
            <div class="mb-3"><label class="form-label">Confirm Password</label><input type="password" name="confirm_password" class="form-control" required></div>
            <button type="submit" class="btn btn-primary w-100"><i class="fas fa-user-plus me-1"></i> Register</button>
        </form>
        <p class="text-center mt-3 text-muted">Already have an account? <a href="${pageContext.request.contextPath}/login">Login here</a></p>
    </div>
</div>
<jsp:include page="includes/footer.jsp" />
