<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Elite Prompt - Create Better AI Prompts. Faster. Smarter."); %>
<jsp:include page="includes/header.jsp" />
<jsp:include page="includes/navbar.jsp" />
<%
    boolean loggedIn = session != null && session.getAttribute("userId") != null;
    String ctx = request.getContextPath();
%>

<section class="hero">
    <div class="container">
        <span class="hero-badge"><i class="fas fa-sparkles me-1"></i> AI Prompt Management System</span>
        <h1>Create Better <span class="gradient-text">AI Prompts.</span><br>Faster. Smarter.</h1>
        <p class="lead mt-3 mb-4">Elite Prompt helps you create, save and manage high-quality<br class="d-none d-md-block"> prompts for your AI workflows.</p>
        <div class="d-flex gap-3 justify-content-center flex-wrap">
            <% if (loggedIn) { %>
                <a href="<%=ctx%>/generator" class="btn btn-primary btn-lg"><i class="fas fa-magic me-2"></i>Create a Prompt</a>
                <a href="<%=ctx%>/dashboard" class="btn btn-outline-light btn-lg"><i class="fas fa-tachometer-alt me-2"></i>Dashboard</a>
            <% } else { %>
                <a href="<%=ctx%>/register" class="btn btn-primary btn-lg"><i class="fas fa-rocket me-2"></i>Create a Prompt</a>
                <a href="#features" class="btn btn-outline-light btn-lg"><i class="fas fa-star me-2"></i>Explore Features</a>
            <% } %>
        </div>
    </div>
</section>

<section id="features" class="py-5">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="section-title">Powerful <span class="gradient-text">Features</span></h2>
            <p class="section-subtitle">Everything you need to master AI prompts</p>
        </div>
        <div class="row g-4">
            <div class="col-md-4"><div class="glass-card h-100 feature-card f1"><div class="feature-icon f1"><i class="fas fa-magic"></i></div><h5>Smart Prompt Generator</h5><p class="text-muted">Generate structured prompts from your requirement using category templates.</p></div></div>
            <div class="col-md-4"><div class="glass-card h-100 feature-card f2"><div class="feature-icon f2"><i class="fas fa-save"></i></div><h5>Save &amp; Organize Prompts</h5><p class="text-muted">Save your best prompts in your personal library (PostgreSQL).</p></div></div>
            <div class="col-md-4"><div class="glass-card h-100 feature-card f3"><div class="feature-icon f3"><i class="fas fa-th-large"></i></div><h5>Multiple Categories</h5><p class="text-muted">Education, Business, Programming, Marketing, Content, Career and more.</p></div></div>
            <div class="col-md-4"><div class="glass-card h-100 feature-card f4"><div class="feature-icon f4"><i class="fas fa-smile"></i></div><h5>Easy to Use</h5><p class="text-muted">Simple forms and clean UI. No technical knowledge required.</p></div></div>
            <div class="col-md-4"><div class="glass-card h-100 feature-card f5"><div class="feature-icon f5"><i class="fas fa-search"></i></div><h5>Search &amp; Filter</h5><p class="text-muted">Find any saved prompt with instant search and category filters.</p></div></div>
            <div class="col-md-4"><div class="glass-card h-100 feature-card f6"><div class="feature-icon f6"><i class="fas fa-chart-line"></i></div><h5>Better AI Results</h5><p class="text-muted">Well-structured prompts give clearer, more accurate AI responses.</p></div></div>
        </div>
    </div>
</section>

<section class="py-5">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="section-title">How It <span class="gradient-text">Works</span></h2>
            <p class="section-subtitle">From idea to perfect prompt in 5 steps</p>
        </div>
        <div class="row g-4 text-center">
            <div class="col-md-4 col-lg-2 offset-lg-1"><div class="glass-card h-100"><div class="step-number s1">1</div><h6>Enter requirement</h6><p class="text-muted small">Describe what you want the AI to do.</p></div></div>
            <div class="col-md-4 col-lg-2"><div class="glass-card h-100"><div class="step-number s2">2</div><h6>Choose category</h6><p class="text-muted small">Education, Business, Coding, etc.</p></div></div>
            <div class="col-md-4 col-lg-2"><div class="glass-card h-100"><div class="step-number s3">3</div><h6>Generate prompt</h6><p class="text-muted small">One click builds a structured prompt.</p></div></div>
            <div class="col-md-6 col-lg-2"><div class="glass-card h-100"><div class="step-number s4">4</div><h6>Copy or save</h6><p class="text-muted small">Copy or save to library.</p></div></div>
            <div class="col-md-6 col-lg-2"><div class="glass-card h-100"><div class="step-number s5">5</div><h6>Use any AI tool</h6><p class="text-muted small">Paste into ChatGPT, Gemini, etc.</p></div></div>
        </div>
    </div>
</section>

<section class="py-5">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="section-title">Built For <span class="gradient-text">Everyone</span></h2>
        </div>
        <div class="row g-4 text-center">
            <div class="col-6 col-md-4 col-lg-2 offset-lg-1"><div class="glass-card"><i class="fas fa-graduation-cap fa-2x mb-2 text-primary"></i><h6>Students</h6></div></div>
            <div class="col-6 col-md-4 col-lg-2"><div class="glass-card"><i class="fas fa-code fa-2x mb-2 text-primary"></i><h6>Developers</h6></div></div>
            <div class="col-6 col-md-4 col-lg-2"><div class="glass-card"><i class="fas fa-pen-nib fa-2x mb-2 text-primary"></i><h6>Creators</h6></div></div>
            <div class="col-6 col-md-6 col-lg-2"><div class="glass-card"><i class="fas fa-briefcase fa-2x mb-2 text-primary"></i><h6>Professionals</h6></div></div>
            <div class="col-12 col-md-6 col-lg-2"><div class="glass-card"><i class="fas fa-store fa-2x mb-2 text-primary"></i><h6>Businesses</h6></div></div>
        </div>
        <div class="text-center mt-5">
            <a href="<%=ctx%>/about.jsp" class="btn btn-outline-light me-2">Learn More</a>
            <a href="<%=ctx%>/register" class="btn btn-primary">Get Started Free</a>
        </div>
    </div>
</section>

<jsp:include page="includes/footer.jsp" />
