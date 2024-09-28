package com.example.crm_gym.dao;

import com.example.crm_gym.models.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainingDAO {
    Optional<Training> save(Training training);
    Optional<Training> update(Training updatedTraining);
    boolean delete(Training training);
    Optional<Training> findById(Long id);
    Optional<List<Training>> findAll();
    Optional<List<Training>> findTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName);
    Optional<List<Training>> findTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName);
}
