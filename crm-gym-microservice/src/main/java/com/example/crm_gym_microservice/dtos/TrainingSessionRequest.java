package com.example.crm_gym_microservice.dtos;

import com.example.crm_gym_microservice.models.ActionType;
import java.time.LocalDate;

public record TrainingSessionRequest(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Boolean isActive,
        LocalDate trainingDate,
        double trainingDuration,
        ActionType actionType) {}