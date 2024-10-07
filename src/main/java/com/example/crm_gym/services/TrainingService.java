package com.example.crm_gym.services;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.logger.TransactionLogger;
import com.example.crm_gym.models.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Transactional
@Service
public class TrainingService extends BaseService<Training> {

    private TrainingDAO trainingDAO;
    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;

    @Autowired
    public TrainingService(TrainingDAO trainingDAO, TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        super(trainingDAO);
        this.trainingDAO = trainingDAO;
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    public Optional<Training> create(String traineeUsername, String trainerUsername, String trainingName,
                                     Date trainingDate, int trainingDuration, String transactionId) {
        try {
            Trainee trainee = traineeDAO.findByUsername(traineeUsername)
                    .orElseThrow(() -> new DaoException("Trainee not found"));
            Trainer trainer = trainerDAO.findByUsername(trainerUsername)
                    .orElseThrow(() -> new DaoException("Trainer not found"));
            Training newTraining = new Training(trainee, trainer, trainingName, trainingDate, trainingDuration);
            Optional<Training> savedTraining = trainingDAO.save(newTraining);
            return savedTraining;
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error creating training", transactionId, e);
            TransactionLogger.logTransactionEnd(transactionId, "Create Training Failed - Exception Occurred");
            throw new ServiceException("Error creating training", e);
        }
    }

    public Training update(Training updatedTraining) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            findEntityById(updatedTraining.getId())
                    .orElseThrow(() -> new ServiceException("Training not found"));
            return trainingDAO.update(updatedTraining);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error updating training with id {}: {}", transactionId, updatedTraining.getId(), e);
            throw new ServiceException("Error updating training with id " + updatedTraining.getId(), e);
        }
    }

    public boolean delete(Long id) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Training training = findEntityById(id)
                    .orElseThrow(() -> new ServiceException("Training not found"));
            return trainingDAO.delete(training);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error deleting training with id {}", transactionId, id, e);
            throw new ServiceException("Error deleting training with id " + id, e);
        }
    }

    public Optional<Training> getTrainingById(Long id) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainingDAO.findById(id);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error fetching training with id {}", transactionId, id, e);
            throw new ServiceException("Error fetching training with id " + id, e);
        }
    }

    public List<Training> getAllTrainings() {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Optional<List<Training>> trainings = trainingDAO.findAll();
            if (trainings.isPresent()) {
                return trainings.get();
            } else {
                log.warn("[Transaction ID: {}] - No trainings found.", transactionId);
                throw new ServiceException("No trainings found");
            }
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error fetching all trainings", transactionId, e);
            throw new ServiceException("Error fetching all trainings", e);
        }
    }

    public Optional<List<Training>> getTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainingDAO.findTrainingsByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error retrieving trainings for trainee username: {}", transactionId, username, e);
            throw new ServiceException("Error retrieving trainings for trainee username: " + username, e);
        }
    }

    public Optional<List<Training>> getTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainingDAO.findTrainingsByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error retrieving trainings for trainer username: {}", transactionId, username, e);
            throw new ServiceException("Error retrieving trainings for trainer username: " + username, e);
        }
    }

}
