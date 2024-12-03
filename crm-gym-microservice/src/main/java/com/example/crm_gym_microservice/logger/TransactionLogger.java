package com.example.crm_gym_microservice.logger;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class TransactionLogger {

    public static String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    public static void logTransactionStart(String transactionId, String service) {
        log.info("Transaction [{}] started for service: {}", transactionId, service);
    }

    public static void logTransactionEnd(String transactionId, String service) {
        log.info("Transaction [{}] ended for service: {}", transactionId, service);
    }

    public static void logRequestDetails(String transactionId, String method, String uri, Map<String, String[]> parameters) {
        log.info("Transaction [{}] - Incoming request: method=[{}], URI=[{}], params=[{}]",
                transactionId, method, uri, formatParameters(parameters));
    }

    public static void logResponseDetails(String transactionId, int statusCode, String responseMessage) {
        log.info("Transaction [{}] - Response: status=[{}], message=[{}]",
                transactionId, statusCode, responseMessage);
    }

    private static String formatParameters(Map<String, String[]> parameters) {
        StringBuilder formattedParams = new StringBuilder();
        parameters.forEach((key, values) -> {
            formattedParams.append(key).append("=").append(Arrays.toString(values)).append(", ");
        });
        return formattedParams.length() > 0 ? formattedParams.substring(0, formattedParams.length() - 2) : "";
    }
}
