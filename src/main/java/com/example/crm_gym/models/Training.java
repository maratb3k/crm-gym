package com.example.crm_gym.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "trainings")
public class Training {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @Column(name = "training_name", nullable = false)
    @Size(min = 5, max = 255, message = "Training name must be between 5 and 255 characters")
    private String trainingName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @Temporal(TemporalType.DATE)
    @Column(name = "training_date", nullable = false)
    private Date trainingDate;

    @Column(name = "training_duration", nullable = false)
    private int trainingDuration;

    public Training() {}

    public Training(String trainingName, TrainingType trainingType, Date trainingDate, int trainingDuration) {
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Training(Trainee trainee, Trainer trainer, String trainingName, TrainingType trainingType, Date trainingDate, int trainingDuration) {
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Long getId() {
        return id;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", trainee=" + trainee +
                ", trainer=" + trainer +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType=" + trainingType +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                '}';
    }
}
