<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Feedback - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    String name = (String) request.getAttribute("name");
    String email = (String) request.getAttribute("email");
    String subject = (String) request.getAttribute("subject");
    String message = (String) request.getAttribute("message");
    if (name == null) name = "";
    if (email == null) email = "";
    if (subject == null) subject = "";
    if (message == null) message = "";
%>
<div class="container py-5">
    <div class="auth-card glass-card" style="max-width:650px;">
        <div class="text-center mb-4">
            <span class="brand-icon"><i class="fas fa-comment-dots"></i></span>
            <h3 class="mt-3">Feedback / Contact Us</h3>
        </div>
        <% if (error != null) { %><div class="alert alert-danger"><%= error %></div><% } %>
        <% if (success != null) { %><div class="alert alert-success"><%= success %></div><% } %>
        <form method="POST" action="${pageContext.request.contextPath}/feedback">
            <div class="row">
                <div class="col-md-6 mb-3"><label class="form-label">Name</label><input type="text" name="name" class="form-control" value="<%= name %>" required></div>
                <div class="col-md-6 mb-3"><label class="form-label">Email</label><input type="email" name="email" class="form-control" value="<%= email %>" required></div>
            </div>
            <div class="mb-3"><label class="form-label">Subject</label><input type="text" name="subject" class="form-control" value="<%= subject %>" required></div>
            <div class="mb-3"><label class="form-label">Message</label><textarea name="message" class="form-control" rows="5" required><%= message %></textarea></div>
            <button type="submit" class="btn btn-primary w-100">Submit Feedback</button>
        </form>
    </div>
</div>
<jsp:include page="includes/footer.jsp" />
