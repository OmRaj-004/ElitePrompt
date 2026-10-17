<%-- Footer include - closes wrapper + footer + scripts --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctxF = request.getContextPath();
    boolean loggedInF = session != null && session.getAttribute("userId") != null;
%>
    </div>
    <footer class="footer" id="footer">
        <div class="container">
            <div class="row">
                <div class="col-lg-4 mb-4 mb-lg-0">
                    <a href="<%=ctxF%>/index.jsp" class="brand-link">
                        <span class="brand-icon me-2"><i class="fas fa-bolt"></i></span>
                        <span class="brand-text">Elite Prompt</span>
                    </a>
                    <p class="mt-3 text-muted">Create Better AI Prompts. Faster. Smarter.</p>
                    <p class="text-muted small">Empowering students, developers, and creators to master AI interactions.</p>
                </div>
                <div class="col-lg-2 col-md-4 mb-4 mb-md-0">
                    <h5 class="footer-heading">Quick Links</h5>
                    <ul class="footer-links">
                        <li><a href="<%=ctxF%>/index.jsp">Home</a></li>
                        <li><a href="<%=ctxF%>/about.jsp">About Us</a></li>
                        <% if (loggedInF) { %>
                            <li><a href="<%=ctxF%>/dashboard">Dashboard</a></li>
                            <li><a href="<%=ctxF%>/generator">Generate Prompt</a></li>
                            <li><a href="<%=ctxF%>/my-prompts">My Prompts</a></li>
                        <% } else { %>
                            <li><a href="<%=ctxF%>/login">Login</a></li>
                            <li><a href="<%=ctxF%>/register">Register</a></li>
                        <% } %>
                        <li><a href="<%=ctxF%>/feedback">Feedback</a></li>
                    </ul>
                </div>
                <div class="col-lg-3 col-md-4 mb-4 mb-md-0">
                    <h5 class="footer-heading">Prompt Categories</h5>
                    <ul class="footer-links">
                        <li>Education</li>
                        <li>Business</li>
                        <li>Content Creation</li>
                        <li>Programming</li>
                        <li>Marketing / Career / General</li>
                    </ul>
                </div>
                <div class="col-lg-3 col-md-4">
                    <h5 class="footer-heading">Connect</h5>
                    <ul class="footer-links">
                        <li><i class="fas fa-envelope me-2"></i>support@eliteprompt.com</li>
                        <li><i class="fas fa-map-marker-alt me-2"></i>TYBCA Mini Project</li>
                        <li><i class="fas fa-users me-2"></i>OmRaj Chaudhary</li>
                    </ul>
                </div>
            </div>
            <div class="row mt-4 pt-3 border-top border-secondary">
                <div class="col-12 text-center">
                    <p class="text-muted mb-0 small">&copy; 2026 Elite Prompt. All rights reserved. | TYBCA Mini Project | Java + PostgreSQL</p>
                </div>
            </div>
        </div>
    </footer>
    <button class="btn btn-primary btn-floating" id="backToTop"><i class="fas fa-arrow-up"></i></button>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<%=ctxF%>/assets/js/script.js"></script>
</body>
</html>
