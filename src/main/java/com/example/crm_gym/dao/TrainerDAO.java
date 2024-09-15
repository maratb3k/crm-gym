package com.example.crm_gym.dao;

import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainerDAO {
    boolean save(Trainer trainer);
    boolean addTrainee(Long trainerId, Long traineeId);
    boolean addTraining(Long trainerId, Long trainingId);
    boolean update(Long id, Trainer trainer);
    boolean updatePassword(Long id, String newPassword);
    boolean updateTrainerUser(Long trainerId, Long userId);
    boolean delete(Long id);
    boolean deleteTrainerUser(Long trainerId);
    boolean deleteByUsername(String username);
    boolean deleteTraineeFromList(Long trainerId, Long traineed);
    boolean deleteTrainingFromList(Long trainerId, Long trainingId);
    Optional<Trainer> findById(Long id);
    Optional<Trainer> findByUsername(String username);
    Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String traineeUsername);
    Optional<List<Trainer>> findAll();
    Optional<List<Training>> findTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName);
}
