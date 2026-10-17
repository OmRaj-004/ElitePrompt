package com.eliteprompt.servlet;

import com.eliteprompt.dao.AdminDAO;
import com.eliteprompt.dao.UserDAO;
import com.eliteprompt.util.AuthUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Admin actions, now across TWO tables:
 *  - remove user      -> delete from users (prompts cascade-delete)
 *  - remove admin     -> delete from admins (demote to nothing)
 *  - react (user)     -> promote: copy credentials from users into admins
 *  - react (admin)    -> demote: delete from admins (user record stays in users)
 * An admin cannot remove or demote themselves.
 */
@WebServlet("/admin-action")
public class AdminActionServlet extends HttpServlet {

    private int parseId(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthUtil.requireAdmin(req, resp)) return;
        int myAdminId = AuthUtil.currentUserId(req.getSession(false));
        int targetId = parseId(req.getParameter("id"));
        String action = req.getParameter("action");
        String message = null;
        String error = null;

        try {
            UserDAO users = new UserDAO();
            AdminDAO admins = new AdminDAO();

            if (targetId <= 0) {
                error = "Invalid id.";
            } else if ("remove".equals(action)) {
                // Remove a normal user (or an admin account)
                if (users.remove(targetId)) {
                    message = "User removed. Their prompts were deleted too.";
                } else if (admins.remove(targetId)) {
                    message = "Admin account removed.";
                } else {
                    error = "User not found.";
                }
            } else if ("react".equals(action)) {
                if (targetId == myAdminId) {
                    error = "You cannot change your own admin role.";
                } else if (admins.remove(targetId)) {
                    message = "Admin demoted to normal user.";
                } else if (users.existsById(targetId) && admins.promote(targetId)) {
                    message = "User promoted to admin.";
                } else {
                    error = "User not found.";
                }
            } else {
                error = "Unknown action.";
            }
        } catch (Exception e) {
            error = "Action failed: " + e.getMessage();
        }

        HttpSession session = req.getSession(false);
        if (session != null) {
            if (message != null) session.setAttribute("adminFlashSuccess", message);
            if (error != null) session.setAttribute("adminFlashError", error);
        }
        resp.sendRedirect("admin");
    }
}
