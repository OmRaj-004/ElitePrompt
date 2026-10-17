<%-- Navbar - changes based on login status --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String currentPage = request.getRequestURI();
    boolean loggedIn = session != null && session.getAttribute("userId") != null;
    String username = loggedIn ? (String) session.getAttribute("username") : "";
    String ctx = request.getContextPath();
%>
<nav class="navbar navbar-expand-lg navbar-dark fixed-top" id="mainNavbar">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center" href="<%=ctx%>/index.jsp">
            <span class="brand-icon me-2"><i class="fas fa-bolt"></i></span>
            <span class="brand-text">Elite Prompt</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center">
                <% if (loggedIn) { %>
                    <li class="nav-item"><a class="nav-link" href="<%=ctx%>/dashboard"><i class="fas fa-tachometer-alt me-1"></i> Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link" href="<%=ctx%>/generator"><i class="fas fa-magic me-1"></i> Generate Prompt</a></li>
                    <li class="nav-item"><a class="nav-link" href="<%=ctx%>/my-prompts"><i class="fas fa-folder me-1"></i> My Prompts</a></li>
                    <li class="nav-item"><a class="nav-link" href="<%=ctx%>/gallery"><i class="fas fa-globe me-1"></i> Gallery</a></li>
                    <% if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) { %>
                        <li class="nav-item"><a class="nav-link" href="<%=ctx%>/admin"><i class="fas fa-shield-alt me-1"></i> Admin</a></li>
                    <% } %>
                    <li class="nav-item"><a class="nav-link" href="<%=ctx%>/feedback"><i class="fas fa-comment-dots me-1"></i> Feedback</a></li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown"><i class="fas fa-user-circle me-1"></i> <%= username %></a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="<%=ctx%>/dashboard">Dashboard</a></li>
                            <li><a class="dropdown-item" href="<%=ctx%>/my-prompts">My Prompts</a></li>
                            <li><a class="dropdown-item" href="<%=ctx%>/settings"><i class="fas fa-key me-2"></i>AI Settings</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="<%=ctx%>/logout">Logout</a></li>
                        </ul>
                    </li>
                <% } else { %>
                    <li class="nav-item"><a class="nav-link" href="<%=ctx%>/index.jsp"><i class="fas fa-home me-1"></i> Home</a></li>
                    <li class="nav-item"><a class="nav-link" href="<%=ctx%>/about.jsp"><i class="fas fa-info-circle me-1"></i> About</a></li>
                    <li class="nav-item"><a class="nav-link" href="<%=ctx%>/gallery"><i class="fas fa-globe me-1"></i> Gallery</a></li>
                    <li class="nav-item"><a class="nav-link" href="<%=ctx%>/feedback"><i class="fas fa-comment-dots me-1"></i> Feedback</a></li>
                    <li class="nav-item ms-lg-2"><a class="btn btn-outline-light" href="<%=ctx%>/login"><i class="fas fa-sign-in-alt me-1"></i> Login</a></li>
                    <li class="nav-item ms-lg-2"><a class="btn btn-primary" href="<%=ctx%>/register"><i class="fas fa-user-plus me-1"></i> Sign Up</a></li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>
<div class="page-wrapper" id="pageContent">
