package com.example.crm_gym_microservice.dtos;

import java.util.List;

public record YearResponse(int year, List<MonthResponse> months) {}
