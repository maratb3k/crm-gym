package com.example.crm_gym.dao;

import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainerDAO {
    Optional<Trainer> save(Trainer trainer);
    boolean addTrainee(Trainer trainer, Trainee trainee);
    boolean addTraining(Trainer trainer, Training training);
    Optional<Trainer> update(Trainer updatedTrainer);
//    boolean updatePassword(Long id, String newPassword);
//    boolean updateTrainerUser(Long trainerId, Long userId);
    boolean delete(Trainer trainer);
    boolean deleteByUsername(String username);
    boolean deleteTraineeFromList(Trainer trainer, Trainee trainee);
    boolean deleteTrainingFromList(Trainer trainer, Training training);
    Optional<Trainer> findById(Long id);
    Optional<Trainer> findByUsername(String username);
    Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String traineeUsername);
    Optional<List<Trainer>> findAll();
    Optional<List<Training>> findTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName);
}
