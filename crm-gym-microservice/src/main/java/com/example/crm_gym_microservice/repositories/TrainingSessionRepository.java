package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
}
