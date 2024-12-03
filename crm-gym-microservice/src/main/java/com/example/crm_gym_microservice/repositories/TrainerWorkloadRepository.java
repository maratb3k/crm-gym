package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
    Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);

    @Query("SELECT COALESCE(SUM(m.trainingSummaryDuration), 0) " +
            "FROM TrainerWorkload w " +
            "JOIN w.years y " +
            "JOIN y.months m " +
            "WHERE w.trainerUsername = :trainerUsername " +
            "AND y.trainingYear = :year " +
            "AND m.trainingMonth = :month")
    double findTotalTrainingDuration(String trainerUsername, int year, int month);
}
