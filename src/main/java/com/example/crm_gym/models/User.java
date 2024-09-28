package com.example.crm_gym.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "userid")
    private Long userId;

    @Column(name = "first_name", nullable = false)
    @Size(min = 5, max = 255, message = "First name must be between 5 and 255 characters")
    @NotNull(message = "First Name is required")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Size(min = 5, max = 255, message = "Last name must be between 5 and 255 characters")
    @NotNull(message = "Last Name is required")
    private String lastName;

    @Column(nullable = false, unique = true)
    @Immutable
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private Trainee trainee;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Trainer trainer;

    public User() {}

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = true;
    }
    public User(String firstName, String lastName, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    public User(String username, String firstName, String lastName, Boolean isActive) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    public User(String firstName, String lastName, String username, String password, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isActive() {
        return isActive;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isActive=" + isActive +
                ", trainee=" + trainee +
                ", trainer=" + trainer +
                '}';
    }
}
