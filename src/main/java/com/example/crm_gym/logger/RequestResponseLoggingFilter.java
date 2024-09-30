package com.example.crm_gym.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class RequestResponseLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, httpServletRequest.getRequestURI());
        logRequestDetails(httpServletRequest, transactionId);
        chain.doFilter(request, response);
        logResponseDetails(httpServletResponse, transactionId);
        TransactionLogger.logTransactionEnd(transactionId, httpServletRequest.getRequestURI());
    }

    private void logRequestDetails(HttpServletRequest request, String transactionId) {
        log.info("Transaction ID: [{}] - Incoming request: method=[{}], URI=[{}], params=[{}]",
                transactionId,
                request.getMethod(),
                request.getRequestURI(),
                request.getParameterMap());
    }

    private void logResponseDetails(HttpServletResponse response, String transactionId) {
        log.info("Transaction ID: [{}] - Response: status=[{}]", transactionId, response.getStatus());
    }
}
