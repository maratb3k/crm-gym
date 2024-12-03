package com.example.crm_gym.dto;

import com.example.crm_gym.models.TrainingTypeName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainingTypeDTO {
    private Long id;
    private TrainingTypeName name;
    private List<TrainingDTO> trainings;
    private List<TrainerDTO> trainers;

    public TrainingTypeDTO(TrainingTypeName name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TrainingTypeName getName() {
        return name;
    }

    public void setName(TrainingTypeName name) {
        this.name = name;
    }

    public List<TrainingDTO> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<TrainingDTO> trainings) {
        this.trainings = trainings;
    }

    public List<TrainerDTO> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerDTO> trainers) {
        this.trainers = trainers;
    }
}
