package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
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
    private UserDAO userDAO;

    @Autowired
    public TrainingService(TrainingDAO trainingDAO, UserDAO userDAO) {
        super(trainingDAO);
        this.trainingDAO = trainingDAO;
        this.userDAO = userDAO;
    }

    public Optional<Training> create(Training training) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainingDAO.save(training);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error creating training", transactionId, e);
            throw e;
        }
    }

    public Training update(Training updatedTraining) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            findEntityById(updatedTraining.getId())
                    .orElseThrow(() -> new DaoException("Training not found"));
            return trainingDAO.update(updatedTraining);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error updating training with id {}: {}", transactionId, updatedTraining.getId(), e);
            throw e;
        }
    }

    public boolean delete(Long id) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Training training = findEntityById(id)
                    .orElseThrow(() -> new DaoException("Training not found"));
            return trainingDAO.delete(training);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error deleting training with id {}", transactionId, id, e);
            return false;
        }
    }

    public Optional<Training> getTrainingById(Long id) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainingDAO.findById(id);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error fetching training with id {}", transactionId, id, e);
            return Optional.empty();
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
                return Collections.emptyList();
            }
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error fetching all trainings", transactionId, e);
            return Collections.emptyList();
        }
    }

    public Optional<List<Training>> getTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainingDAO.findTrainingsByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error retrieving trainings for trainee username: {}", transactionId, username, e);
            return Optional.empty();
        }
    }

    public Optional<List<Training>> getTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainingDAO.findTrainingsByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error retrieving trainings for trainer username: {}", transactionId, username, e);
            return Optional.empty();
        }
    }

}
