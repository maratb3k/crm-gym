package com.example.crm_gym.dao;

import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TraineeDAO extends BaseDAO<Trainee> {
    Optional<Trainee> save(Trainee trainee);
    boolean addTrainer(Trainee trainee, Trainer trainer);
    boolean addTraining(Trainee trainee, Training training);
    Optional<Trainee> update(Trainee updatedTrainee);
    boolean delete(Trainee trainee);
    boolean deleteByUsername(String username);
    boolean deleteTrainerFromList(Trainee trainee, Trainer trainer);
    boolean deleteTrainingFromList(Trainee trainee, Training training);
    Optional<Trainee> findById(Long id);
    Optional<Trainee> findByUsername(String username);
    Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String traineeUsername);
    Optional<List<Training>> findTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName);
    Optional<List<Trainee>> findAll();
}
