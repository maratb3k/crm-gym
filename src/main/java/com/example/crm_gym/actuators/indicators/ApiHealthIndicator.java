package com.example.crm_gym.actuators.indicators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class ApiHealthIndicator implements HealthIndicator {

    @Autowired
    @Lazy
    private RestTemplate restTemplate;

    @Override
    public Health health() {
        String url = "http://localhost:8080";
        try {
            restTemplate.getForObject(url, String.class);
            return Health.up().withDetail("API", "Available").build();
        } catch (HttpServerErrorException serverError) {
            return Health.down(serverError)
                    .withDetail("API", "Server error")
                    .withDetail("statusCode", serverError.getStatusCode())
                    .withDetail("body", serverError.getResponseBodyAsString())
                    .build();
        } catch (HttpClientErrorException clientError) {
            return Health.down(clientError)
                    .withDetail("API", "Client error")
                    .withDetail("statusCode", clientError.getStatusCode())
                    .withDetail("body", clientError.getResponseBodyAsString())
                    .build();
        } catch (ResourceAccessException accessException) {
            return Health.down(accessException)
                    .withDetail("API", "Not reachable")
                    .withDetail("error", accessException.getMessage())
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("API", "Unknown error")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
