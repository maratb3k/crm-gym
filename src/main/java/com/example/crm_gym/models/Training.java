package com.example.crm_gym.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Training {
    private int id;
    private int traineeId;
    private int trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private Date trainingDate;
    private String trainingDuration;
}
