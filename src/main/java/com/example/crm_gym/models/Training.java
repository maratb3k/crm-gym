package com.example.crm_gym.models;

import java.util.Date;

public class Training {
    private int id;
    private int traineeId;
    private int trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private Date trainingDate;
    private String trainingDuration;

    public Training(int id, int traineeId, int trainerId, String trainingName, TrainingType trainingType, Date trainingDate, String trainingDuration) {
        this.id = id;
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public int getId() {
        return id;
    }

    public int getTraineeId() {
        return traineeId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public String getTrainingDuration() {
        return trainingDuration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTraineeId(int traineeId) {
        this.traineeId = traineeId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    public void setTrainingDuration(String trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}
