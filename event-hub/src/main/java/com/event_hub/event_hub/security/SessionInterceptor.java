package com.event_hub.event_hub.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        // 1. Check if the user is authenticated via session variables
        if (session == null || session.getAttribute("user") == null) {
            String path = request.getRequestURI();

            // Allow unauthenticated traffic to pass through registration, logins, and public static resources
            if (path.equals("/login") || path.equals("/register") || path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images")) {
                return true;
            }

            // Send unauthenticated users straight to the login endpoint
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HttpSession session = request.getSession(false);

        // 2. Automatically seed the global view model with user context properties for Thymeleaf
        if (session != null && session.getAttribute("user") != null && modelAndView != null) {
            String loggedInUser = (String) session.getAttribute("user");
            modelAndView.addObject("currentUser", loggedInUser);
        }
    }
}
