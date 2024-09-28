package com.example.crm_gym.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers")
public class Trainer {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingType specialization;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @ManyToMany(mappedBy = "trainers")
    private Set<Trainee> trainees = new HashSet<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Training> trainings = new HashSet<>();

    public Trainer() {}

    public Trainer(TrainingType specialization) {
        this.specialization = specialization;
    }

    public Trainer(TrainingType specialization, User user) {
        this.specialization = specialization;
        this.user = user;
    }

    public Trainer(TrainingType specialization, User user, Set<Trainee> trainees, Set<Training> trainings) {
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

    public Set<Training> getTrainings() {
        return trainings;
    }

    public Set<Trainee> getTrainees() {
        return trainees;
    }

    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTrainees(Set<Trainee> trainees) {
        this.trainees = trainees;
    }

    public void setTrainings(Set<Training> trainings) {
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
