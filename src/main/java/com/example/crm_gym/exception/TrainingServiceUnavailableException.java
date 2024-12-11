package com.example.crm_gym.exception;

public class TrainingServiceUnavailableException extends RuntimeException {
    public TrainingServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
