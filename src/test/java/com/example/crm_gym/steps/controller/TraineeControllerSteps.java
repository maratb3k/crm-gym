package com.example.crm_gym.steps.controller;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TraineeControllerSteps {

    private String basePath = "http://localhost:8081/";
    private String url;
    private String requestBody;
    private ResponseEntity<String> response;
    private String token;

    @Autowired
    private RestTemplate restTemplate;

    @Given("trainee calls endpoint {string} with the following JSON request body:")
    public void givenClientCallsEndpointWithRequestBody(String endpoint, String jsonRequest) {
        this.url = basePath + "/" + endpoint;
        this.requestBody = jsonRequest;
    }

    @When("the trainee sends a POST request in order to authenticate")
    public void theTraineeSendsAPOSTRequestInOrderToAuthenticate() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            response = restTemplate.postForEntity(url, entity, String.class);
            token = response.getBody().trim();
        } catch (HttpClientErrorException e) {
            response = new ResponseEntity<>(e.getStatusCode());
        }
    }

    @Then("the authentication response status code should be {int}")
    public void checkAuthResponseStatusCode(int expectedStatusCode) {
        Assertions.assertEquals(expectedStatusCode, response.getStatusCodeValue());
    }
}
