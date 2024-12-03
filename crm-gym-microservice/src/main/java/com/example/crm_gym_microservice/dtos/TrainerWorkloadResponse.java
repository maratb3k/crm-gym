package com.example.crm_gym_microservice.dtos;

import java.util.List;

public record TrainerWorkloadResponse(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Boolean isActive,
        List<YearResponse> years) {}