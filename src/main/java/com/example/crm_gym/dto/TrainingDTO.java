package com.example.crm_gym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainingDTO {
    private Long id;
    private TraineeDTO trainee;
    private TrainerDTO trainer;
    private String trainingName;
    private TrainingTypeDTO trainingType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date trainingDate;
    private int trainingDuration;

    public TrainingDTO(Long id, String trainingName) {
        this.id = id;
        this.trainingName = trainingName;
    }

    public TrainingDTO(String trainingName, Date trainingDate, TrainingTypeDTO trainingType, int trainingDuration, TrainerDTO trainer) {
        this.trainingName = trainingName;
        this.trainingDate = trainingDate;
        this.trainingType = trainingType;
        this.trainingDuration = trainingDuration;
        this.trainer = trainer;
    }

    public TrainingDTO(String trainingName, Date trainingDate, TrainingTypeDTO trainingType, int trainingDuration, TraineeDTO trainee) {
        this.trainingName = trainingName;
        this.trainingDate = trainingDate;
        this.trainingType = trainingType;
        this.trainingDuration = trainingDuration;
        this.trainee = trainee;
    }

    public TrainingDTO(Long id, String trainingName, Date trainingDate, int trainingDuration, TraineeDTO trainee, TrainerDTO trainer, TrainingTypeDTO trainingType) {
        this.id = id;
        this.trainingName = trainingName;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingType = trainingType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TraineeDTO getTrainee() {
        return trainee;
    }

    public void setTrainee(TraineeDTO trainee) {
        this.trainee = trainee;
    }

    public TrainerDTO getTrainer() {
        return trainer;
    }

    public void setTrainer(TrainerDTO trainer) {
        this.trainer = trainer;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingTypeDTO getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingTypeDTO trainingType) {
        this.trainingType = trainingType;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}
