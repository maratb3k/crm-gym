package com.example.crm_gym.models;

public enum TrainingType {
    YOGA,
    CARDIO,
    STRENGTH_TRAINING,
    PILATES,
    FITNESS;

    public static TrainingType fromString(String type) {
        try {
            return TrainingType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TrainingType: " + type);
        }
    }
}
