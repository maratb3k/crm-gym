package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingTypeDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Transactional
@Service
public class TrainingTypeService {

    private TrainingTypeDAO trainingTypeDAO;
    private UserDAO userDAO;

    @Autowired
    public TrainingTypeService(TrainingTypeDAO trainingTypeDAO, UserDAO userDAO) {
        this.trainingTypeDAO = trainingTypeDAO;
        this.userDAO = userDAO;
    }

    public boolean create(TrainingTypeName name) {
        try {
            TrainingType trainingType = new TrainingType(name);
            return trainingTypeDAO.save(trainingType);
        } catch (DaoException e) {
            log.error("Error creating training type", e);
            return false;
        }
    }

    public boolean update(String username, String password, Long id, TrainingType trainingType) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            return trainingTypeDAO.update(id, trainingType);
        } catch (DaoException e) {
            log.error("Error updating training type with id {}: {}", id, e);
            return false;
        }
    }

    public boolean delete(String username, String password, Long id) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            return trainingTypeDAO.delete(id);
        } catch (DaoException e) {
            log.error("Error deleting training type with id {}", id, e);
            return false;
        }
    }

    public Optional<TrainingType> getTrainingTypeById(String username, String password, Long id) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return trainingTypeDAO.findById(id);
        } catch (DaoException e) {
            log.error("Error fetching training type with id {}", id, e);
            return Optional.empty();
        }
    }

    public List<TrainingType> getAllTrainingTypes(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Collections.emptyList();
            }
            Optional<List<TrainingType>> trainingTypes = trainingTypeDAO.findAll();
            if (trainingTypes.isPresent()) {
                return trainingTypes.get();
            } else {
                log.warn("No training types found.");
                return Collections.emptyList();
            }
        } catch (DaoException e) {
            log.error("Error fetching all training types", e);
            return Collections.emptyList();
        }
    }

}
