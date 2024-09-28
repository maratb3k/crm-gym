package com.example.crm_gym.dao;

import com.example.crm_gym.models.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeDAO {
    Optional<TrainingType> save(TrainingType trainingType);
    Optional<TrainingType> update(TrainingType updatedTrainingType);
    boolean delete(TrainingType trainingType);
    Optional<TrainingType> findById(Long id);
    Optional<List<TrainingType>> findAll();
}
