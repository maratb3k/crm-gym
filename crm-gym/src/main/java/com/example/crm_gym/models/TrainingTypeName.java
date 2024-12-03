package com.example.crm_gym.models;

public enum TrainingTypeName {
    CARDIO,
    STRENGTH,
    HIIT,
    YOGA,
    PILATES,
    CROSSFIT,
    BODYBUILDING,
    KICKBOXING,
    DANCE,
    REHABILITATION,
    FITNESS;

    public static TrainingTypeName fromString(String type) {
        try {
            return TrainingTypeName.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TrainingTypeName: " + type);
        }
    }
}
