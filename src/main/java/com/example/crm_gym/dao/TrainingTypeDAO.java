package com.example.crm_gym.dao;

import com.example.crm_gym.models.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeDAO {
    boolean save(TrainingType trainingType);
    boolean update(Long id, TrainingType trainingType);
    boolean delete(Long id);
    Optional<TrainingType> findById(Long id);
    Optional<List<TrainingType>> findAll();
}
