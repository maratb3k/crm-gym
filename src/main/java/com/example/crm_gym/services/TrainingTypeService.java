package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingTypeDAO;
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
public class TrainingTypeService extends BaseService<TrainingType> {

    private TrainingTypeDAO trainingTypeDAO;
    private UserDAO userDAO;

    @Autowired
    public TrainingTypeService(TrainingTypeDAO trainingTypeDAO, UserDAO userDAO) {
        super(trainingTypeDAO);
        this.trainingTypeDAO = trainingTypeDAO;
        this.userDAO = userDAO;
    }

    public Optional<TrainingType> create(TrainingTypeName name) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            TrainingType trainingType = new TrainingType(name);
            Optional<TrainingType> savedTrainingType = trainingTypeDAO.save(trainingType);
            return savedTrainingType;
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error creating training type", transactionId, e);
            throw e;
        }
    }

    public Optional<TrainingType> update(TrainingType newTrainingType) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            findEntityById(newTrainingType.getId())
                    .orElseThrow(() -> new DaoException("Training type not found"));
            return trainingTypeDAO.update(newTrainingType);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error updating training type with id {}: {}", transactionId, newTrainingType.getId(), e);
            throw e;
        }
    }

    public boolean delete(Long id) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            TrainingType trainingType = findEntityById(id)
                    .orElseThrow(() -> new DaoException("Training Type not found"));
            return trainingTypeDAO.delete(trainingType);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error deleting training type with id {}", transactionId, id, e);
            return false;
        }
    }

    public Optional<TrainingType> getTrainingTypeById(Long id) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainingTypeDAO.findById(id);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error fetching training type with id {}", transactionId, id, e);
            return Optional.empty();
        }
    }

    public List<TrainingType> getAllTrainingTypes() {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Optional<List<TrainingType>> trainingTypes = trainingTypeDAO.findAll();
            if (trainingTypes.isPresent()) {
                return trainingTypes.get();
            } else {
                log.warn("[Transaction ID: {}] - No training types found.", transactionId);
                return Collections.emptyList();
            }
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error fetching all training types", transactionId, e);
            return Collections.emptyList();
        }
    }

}
