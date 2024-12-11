package com.example.crm_gym.producer;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ResponseRegistry {

    private final ConcurrentMap<String, CompletableFuture<Double>> responseMap = new ConcurrentHashMap<>();

    public CompletableFuture<Double> createFutureForTransaction(String transactionId) {
        CompletableFuture<Double> future = new CompletableFuture<>();
        responseMap.put(transactionId, future);
        return future;
    }

    public CompletableFuture<Double> getFutureForTransaction(String transactionId) {
        return responseMap.remove(transactionId);
    }

    public void removeTransaction(String transactionId) {
        responseMap.remove(transactionId);
    }
}
