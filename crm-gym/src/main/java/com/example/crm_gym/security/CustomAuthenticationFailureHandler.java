package com.example.crm_gym.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler  {

    @Autowired
    private BruteForceProtectionService bruteForceProtectionService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        bruteForceProtectionService.loginFailed(username);

        String errorMessage;
        if (exception instanceof LockedException) {
            errorMessage = "Your account has been locked due to multiple failed login attempts. Please try again after 5 minutes.";
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid username or password.";
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            errorMessage = "Authentication failed.";
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
        response.getWriter().flush();
    }
}
