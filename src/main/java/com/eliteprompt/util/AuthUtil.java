package com.eliteprompt.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * AuthUtil - helper for session-based authentication.
 */
public class AuthUtil {

    public static boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute("userId") != null;
    }

    public static int currentUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (id instanceof Integer) return (Integer) id;
        return -1;
    }

    public static boolean isAdmin(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("isAdmin"));
    }

    /** Redirect non-admins to dashboard (used by admin-only pages). */
    public static boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (!isLoggedIn(session)) {
            resp.sendRedirect("login");
            return false;
        }
        if (!isAdmin(session)) {
            resp.sendRedirect("dashboard");
            return false;
        }
        return true;
    }
}
