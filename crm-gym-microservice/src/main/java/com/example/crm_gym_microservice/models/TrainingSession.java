package com.example.crm_gym_microservice.models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;

    private LocalDate trainingDate;
    private double trainingDuration;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;
}
