package com.example.crm_gym.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Missing or invalid Authorization header.\"}");
            return false;
        }
        String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);

        final String[] values = credentials.split(":", 2);
        String username = values[0];
        String password = values[1];

        boolean isAuthenticated = userService.authenticateUser(username, password);
        if (!isAuthenticated) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid username or password.\"}");
            return false;
        }

        return true;
    }
}