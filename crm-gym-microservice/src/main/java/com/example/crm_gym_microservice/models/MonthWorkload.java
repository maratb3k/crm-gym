package com.example.crm_gym_microservice.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthWorkload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int trainingMonth;
    private double trainingSummaryDuration;

    @ManyToOne
    @JoinColumn(name = "year_workload_id")
    private YearWorkload yearWorkload;
}
