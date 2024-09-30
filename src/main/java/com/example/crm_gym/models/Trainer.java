package com.example.crm_gym.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainers")
public class Trainer {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingType specialization;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @ManyToMany(mappedBy = "trainers", fetch = FetchType.EAGER)
    private List<Trainee> trainees = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Training> trainings = new ArrayList<>();

    public Trainer() {}

    public Trainer(User user) {
        this.user = user;
    }

    public Trainer(TrainingType specialization, User user) {
        this.specialization = specialization;
        this.user = user;
    }

    public Trainer(TrainingType specialization, User user, List<Trainee> trainees, List<Training> trainings) {
        this.specialization = specialization;
        this.user = user;
        this.trainees = trainees;
    }

    public Long getId() {
        return id;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }

    public User getUser() {
        return user;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public List<Trainee> getTrainees() {
        return trainees;
    }

    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTrainees(List<Trainee> trainees) {
        this.trainees = trainees;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + id +
                ", specialization=" + specialization +
                ", user=" + user +
                ", trainees=" + trainees +
                ", trainings=" + trainings +
                '}';
    }
}
