<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "About - Elite Prompt"); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />

<div class="container py-5">
    <div class="text-center mb-5">
        <h1>About <span class="gradient-text">Elite Prompt</span></h1>
        <p class="section-subtitle">Create Better AI Prompts. Faster. Smarter. (Java + PostgreSQL Edition)</p>
    </div>
    <div class="row g-4">
        <div class="col-12"><div class="glass-card">
            <h4><i class="fas fa-lightbulb me-2 text-warning"></i>What is Elite Prompt?</h4>
            <p class="text-muted">Elite Prompt helps students, developers, creators and professionals create, save, organize and manage AI prompts. This version is built with <strong>Java Servlets + JSP + PostgreSQL</strong>. No real AI API is used - the generator builds prompts from predefined templates per category.</p>
        </div></div>
        <div class="col-md-6"><div class="glass-card h-100">
            <h4><i class="fas fa-exclamation-circle me-2 text-danger"></i>The Problem</h4>
            <ul class="text-muted"><li>Beginners can't write effective AI prompts.</li><li>Vague prompts give poor answers.</li><li>Good prompts are lost, no place to save them.</li><li>No organization by category or tone.</li></ul>
        </div></div>
        <div class="col-md-6"><div class="glass-card h-100">
            <h4><i class="fas fa-check-circle me-2 text-success"></i>Our Solution</h4>
            <ul class="text-muted"><li>Simple form: title, category, requirement, tone, format.</li><li>Template generator (PromptGenerator.java) builds prompt instantly.</li><li>PostgreSQL library to save, search, edit, delete.</li><li>Copy and use with ChatGPT / Gemini.</li></ul>
        </div></div>
        <div class="col-md-6"><div class="glass-card h-100">
            <h4><i class="fas fa-bullseye me-2 text-primary"></i>Objectives</h4>
            <ul class="text-muted"><li>Help users write better prompts quickly.</li><li>Category-wise structures.</li><li>Secure login with BCrypt + sessions.</li><li>CRUD with PreparedStatement.</li><li>Simple, secure, responsive.</li></ul>
        </div></div>
        <div class="col-md-6"><div class="glass-card h-100">
            <h4><i class="fas fa-layer-group me-2 text-primary"></i>Technology Used</h4>
            <ul class="text-muted">
                <li>Java 26 + Servlets &amp; JSP (Jakarta EE 11, Tomcat 11)</li>
                <li>PostgreSQL 18 with JDBC (DAO classes + PreparedStatement)</li>
                <li>BCrypt password hashing (jBCrypt library)</li>
                <li>HttpSession for login sessions</li>
                <li>Bootstrap 5 dark AI-themed responsive UI</li>
            </ul>
        </div></div>
        <div class="col-12"><div class="glass-card text-center">
            <h4>Project Team</h4>
            <p class="text-muted"><strong>OmRaj Chaudhary</strong></p>
            <p class="text-muted small">TYBCA Mini Project | Java, JSP, Servlets, PostgreSQL, Bootstrap</p>
            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary mt-2">Try Elite Prompt</a>
        </div></div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
