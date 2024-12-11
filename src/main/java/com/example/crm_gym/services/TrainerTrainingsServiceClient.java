package com.example.crm_gym.services;

import com.example.crm_gym.dto.TrainingSessionRequestDTO;
import com.example.crm_gym.exception.TrainingServiceUnavailableException;
import com.example.crm_gym.producer.ResponseRegistry;
import com.example.crm_gym.producer.TrainerTrainingMessageProducer;
import com.example.crm_gym.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.MapMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.concurrent.*;

@Service
@Slf4j
public class TrainerTrainingsServiceClient {
    private final TrainerTrainingMessageProducer messageProducer;
    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;
    private final ResponseRegistry responseRegistry;

    @Autowired
    public TrainerTrainingsServiceClient(JmsTemplate jmsTemplate, TrainerTrainingMessageProducer messageProducer, ObjectMapper objectMapper, ResponseRegistry responseRegistry) {
        this.messageProducer = messageProducer;
        this.objectMapper = objectMapper;
        this.jmsTemplate = jmsTemplate;
        this.responseRegistry = responseRegistry;
    }

    @CircuitBreaker(name = "trainingService", fallbackMethod = "fallback")
    public void sendTrainingSession(TrainingSessionRequestDTO request, String transactionId) {
        try {
            String token = JwtAuthenticationFilter.getCurrentToken();
            String jsonPayload = objectMapper.writeValueAsString(request);
            messageProducer.sendTrainingSessionMessage("training-session-queue", jsonPayload, transactionId, token);
        } catch (HttpClientErrorException.Forbidden e) {
            log.error("[Transaction ID: {}] - Authorization failed. Forbidden response from TrainingService: {}", transactionId, e.getMessage());
            throw new TrainingServiceUnavailableException("Authorization failed. Please check the token.", e);
        } catch (TrainingServiceUnavailableException e) {
            log.error("[Transaction ID: {}] - Training Service Unavailable: {}", transactionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error communicating with TrainingService: {}", transactionId, e.getMessage());
            throw new TrainingServiceUnavailableException("Error communicating with TrainingService", e);
        }
    }

    @CircuitBreaker(name = "trainingService", fallbackMethod = "fallbackGetTrainerMonthlyDuration")
    public double getTrainerMonthlyDuration(String username, int year, int month, String transactionId) {
        try {
            String token = JwtAuthenticationFilter.getCurrentToken();
            jmsTemplate.send("trainer-duration-request-queue", session -> {
                MapMessage message = session.createMapMessage();
                message.setString("username", username);
                message.setInt("year", year);
                message.setInt("month", month);
                message.setString("transactionId", transactionId);
                message.setStringProperty("Authorization", "Bearer " + token);
                log.info("[Transaction ID: {}] Sent request to training-service: {}", transactionId, message);
                return message;
            });
            CompletableFuture<Double> futureResponse = responseRegistry.createFutureForTransaction(transactionId);
            return futureResponse.get(30, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("[Transaction ID: {}] - Timeout waiting for response from TrainingService", transactionId);
            throw new RuntimeException("Training Service timeout", e);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error fetching workload from TrainingService: {}", transactionId, e.getMessage());
            throw new RuntimeException("Error fetching monthly training duration from Training Service", e);
        } finally {
            responseRegistry.removeTransaction(transactionId);
        }
    }

    private double waitForResponse(String transactionId) {
        CompletableFuture<Double> futureResponse = responseRegistry.createFutureForTransaction(transactionId);
        try {
            return futureResponse.get(30, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("[Transaction ID: {}] - Timeout waiting for response from TrainingService", transactionId);
            throw new RuntimeException("Training Service timeout", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void fallback(TrainingSessionRequestDTO request, String transactionId, Throwable throwable) {
        log.error("[Transaction ID: {}] - Circuit breaker triggered for TrainingService. Falling back. Reason: {}", transactionId, throwable.getMessage());
    }

    public double fallbackGetTrainerMonthlyDuration(String username, int year, int month, String transactionId, Throwable throwable) {
        log.error("[Transaction ID: {}] - Fallback triggered for getTrainerMonthlyDuration. Username: {}, Year: {}, Month: {}. Reason: {}",
                transactionId, username, year, month, throwable.getMessage());
        throw new TrainingServiceUnavailableException("Training Service is unavailable. Please try again later.", throwable);
    }
}
