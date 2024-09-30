package com.example.crm_gym.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainerDTO {
    private Long id;
    private TrainingTypeDTO specialization;
    private UserDTO user;
    private List<TraineeDTO> trainees;
    private List<TrainingDTO> trainings;

    public TrainerDTO(UserDTO user) {
        this.user = user;
    }

    public TrainerDTO(UserDTO user, TrainingTypeDTO specialization) {
        this.user = user;
        this.specialization = specialization;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TrainingTypeDTO getSpecialization() {
        return specialization;
    }

    public void setSpecialization(TrainingTypeDTO specialization) {
        this.specialization = specialization;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public List<TraineeDTO> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<TraineeDTO> trainees) {
        this.trainees = trainees;
    }

    public List<TrainingDTO> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<TrainingDTO> trainings) {
        this.trainings = trainings;
    }
}
