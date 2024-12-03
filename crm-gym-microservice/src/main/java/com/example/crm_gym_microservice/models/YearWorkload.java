package com.example.crm_gym_microservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearWorkload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int trainingYear;

    @ManyToOne
    @JoinColumn(name = "trainer_workload_id")
    private TrainerWorkload trainerWorkload;

    @OneToMany(mappedBy = "yearWorkload", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MonthWorkload> months;
}