package com.example.crm_gym.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "training_types")
public class TrainingType {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private TrainingTypeName name;

    @OneToMany(mappedBy = "trainingType", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Training> trainings;

    @OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Trainer> trainers;

    public TrainingType() {}

    public TrainingType(TrainingTypeName name) {
        this.name = name;
    }

    public TrainingType(TrainingTypeName name, List<Training> trainings, List<Trainer> trainers) {
        this.name = name;
        this.trainings = trainings;
        this.trainers = trainers;
    }

    public Long getId() {
        return id;
    }

    public TrainingTypeName getName() {
        return name;
    }

    public void setName(TrainingTypeName name) {
        this.name = name;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }

    public List<Trainer> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<Trainer> trainers) {
        this.trainers = trainers;
    }

    @Override
    public String toString() {
        return "TrainingType{" +
                "id=" + id +
                ", name=" + name +
                ", trainings=" + trainings +
                ", trainers=" + (trainers != null ? "Trainers size: " + trainers.size() : null) +
                '}';
    }
}