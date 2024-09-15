package com.example.crm_gym.dao;

import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TraineeDAO {
    boolean save(Trainee trainee);
    boolean addTrainer(Long traineeId, Long trainerId);
    boolean addTraining(Long traineeId, Long trainingId);
    boolean update(Long id, Trainee trainee);
    boolean updatePassword(Long id, String newPassword);
    boolean updateTrainersList(Long id, Set<Trainer> trainers);
    boolean updateTraineeUser(Long traineeId, Long userId);
    boolean delete(Long id);
    boolean deleteByUsername(String username);
    boolean deleteTraineeUser(Long traineeId);
    boolean deleteTrainerFromList(Long traineeId, Long trainerId);
    boolean deleteTrainingFromList(Long traineeId, Long trainingId);
    Optional<Trainee> findById(Long id);
    Optional<Trainee> findByUsername(String username);
    Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String traineeUsername);
    Optional<List<Training>> findTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName);
    Optional<List<Trainee>> findAll();
}
