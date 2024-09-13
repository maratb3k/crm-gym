package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingDAO;
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
public class TrainingService {

    private TrainingDAO trainingDAO;
    private UserDAO userDAO;

    @Autowired
    public TrainingService(TrainingDAO trainingDAO, UserDAO userDAO) {
        this.trainingDAO = trainingDAO;
        this.userDAO = userDAO;
    }

    public boolean create(String trainingName, TrainingType trainingType, Date trainingDate, int trainingDuration) {
        try {
            Training training = new Training(trainingName, trainingType, trainingDate, trainingDuration);
            return trainingDAO.save(training);
        } catch (DaoException e) {
            log.error("Error creating training", e);
            return false;
        }
    }

    public boolean update(String username, String password, Long id, Training training) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            return trainingDAO.update(id, training);
        } catch (DaoException e) {
            log.error("Error updating training with id {}: {}", id, e);
            return false;
        }
    }

    public boolean delete(String username, String password, Long id) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            return trainingDAO.delete(id);
        } catch (DaoException e) {
            log.error("Error deleting training with id {}", id, e);
            return false;
        }
    }

    public Optional<Training> getTrainingById(String username, String password, Long id) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return trainingDAO.findById(id);
        } catch (DaoException e) {
            log.error("Error fetching training with id {}", id, e);
            return Optional.empty();
        }
    }

    public List<Training> getAllTrainings(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Collections.emptyList();
            }
            Optional<List<Training>> trainings = trainingDAO.findAll();
            if (trainings.isPresent()) {
                return trainings.get();
            } else {
                log.warn("No trainings found.");
                return Collections.emptyList();
            }
        } catch (DaoException e) {
            log.error("Error fetching all trainings", e);
            return Collections.emptyList();
        }
    }

    public List<Training> getTrainingsByTraineeUsernameAndCriteria(String username, String password,Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Collections.emptyList();
            }
            Optional<List<Training>> trainings = trainingDAO.findTrainingsByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
            if (trainings.isPresent()) {
                return trainings.get();
            } else {
                log.warn("No trainings found for trainee username: {}", username);
                return Collections.emptyList();
            }
        } catch (DaoException e) {
            log.error("Error retrieving trainings for trainee username: {}", username, e);
            return Collections.emptyList();
        }
    }

    public Optional<List<Training>> getTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName) {
        try {
            return trainingDAO.findTrainingsByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
        } catch (DaoException e) {
            log.error("Error retrieving trainings for trainer username: {}", username, e);
            return Optional.empty();
        }
    }

}
