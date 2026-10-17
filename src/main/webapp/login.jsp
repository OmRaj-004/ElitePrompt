<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Login - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    String error = (String) request.getAttribute("error");
    String login_input = (String) request.getAttribute("login_input");
    if (login_input == null) login_input = "";
%>
<div class="container">
    <div class="auth-card glass-card">
        <div class="text-center mb-4">
            <span class="brand-icon"><i class="fas fa-sign-in-alt"></i></span>
            <h3 class="mt-3">Welcome Back</h3>
            <p class="text-muted">Login to your account</p>
        </div>
        <% if (error != null) { %><div class="alert alert-danger"><%= error %></div><% } %>
        <form method="POST" action="${pageContext.request.contextPath}/login">
            <div class="mb-3"><label class="form-label">Email or Username</label><input type="text" name="login_input" class="form-control" value="<%= login_input %>" required></div>
            <div class="mb-3"><label class="form-label">Password</label><input type="password" name="password" class="form-control" required></div>
            <button type="submit" class="btn btn-primary w-100"><i class="fas fa-sign-in-alt me-1"></i> Login</button>
        </form>
        <p class="text-center mt-3 text-muted">No account? <a href="${pageContext.request.contextPath}/register">Register here</a></p>
    </div>
</div>
<jsp:include page="includes/footer.jsp" />
