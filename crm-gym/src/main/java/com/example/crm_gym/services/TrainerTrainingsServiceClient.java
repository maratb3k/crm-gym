package com.example.crm_gym.services;

import com.example.crm_gym.dto.TrainingSessionRequestDTO;
import com.example.crm_gym.exception.TrainingServiceUnavailableException;
import com.example.crm_gym.security.JwtAuthenticationFilter;
import com.example.crm_gym.logger.TransactionLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
@Slf4j
public class TrainerTrainingsServiceClient {
    private final RestTemplate restTemplate;

    @Autowired
    public TrainerTrainingsServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "trainingService", fallbackMethod = "fallback")
    public void sendTrainingSession(TrainingSessionRequestDTO request, String transactionId) {
        try {
            String userToken = JwtAuthenticationFilter.getCurrentToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            headers.set("TransactionID", transactionId);
            HttpEntity<TrainingSessionRequestDTO> entity = new HttpEntity<>(request, headers);
            String url = "http://training-service/trainings";
            restTemplate.postForEntity(url, entity, Void.class);
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
            String userToken = JwtAuthenticationFilter.getCurrentToken();
            String url = String.format("http://training-service/trainings/%s/%d/%d", username, year, month);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            headers.set("TransactionID", transactionId);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Double> response = restTemplate.exchange(url, HttpMethod.GET, entity, Double.class);
            if (response.getBody() == null) {
                log.warn("[Transaction ID: {}] - Response body is null for username: {}, year: {}, month: {}", transactionId, username, year, month);
                return 0.0;
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error fetching workload from TrainingService: {}", transactionId, e.getMessage());
            throw new RuntimeException("Error fetching monthly training duration from Training Service", e);
        }
    }

    public void fallback(TrainingSessionRequestDTO request, Throwable throwable) {
        log.error("Circuit breaker triggered for TrainingService. Falling back. Reason: {}", throwable.getMessage());
    }

    public double fallbackGetTrainerMonthlyDuration(String username, int year, int month, Throwable throwable) {
        log.error("Fallback triggered for getTrainerMonthlyDuration. Username: {}, Year: {}, Month: {}. Reason: {}",
                username, year, month, throwable.getMessage());
        throw new TrainingServiceUnavailableException("Training Service is unavailable. Please try again later.", throwable);
    }
}
