package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.models.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.example.crm_gym.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Transactional
@Service
public class TrainingService {

    private TrainingDAO trainingDAO;

    @Autowired
    public TrainingService(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    public Optional<Training> create(Training training) {
        try {
            return trainingDAO.save(training);
        } catch (Exception e) {
            log.error("Error creating training", e);
            throw new ServiceException("Error creating training", e);
        }
    }

    public Optional<Training> update(Training updatedTraining) {
        try {
            trainingDAO.findById(updatedTraining.getId())
                    .orElseThrow(() -> new ServiceException("Training not found"));
            return trainingDAO.update(updatedTraining);
        } catch (Exception e) {
            log.error("Error updating training with id {}: {}", updatedTraining.getId(), e);
            throw new ServiceException("Error updating training with id " + updatedTraining.getId(), e);
        }
    }

    public boolean delete(Long id) {
        try {
            Training training = trainingDAO.findById(id)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            return trainingDAO.delete(training);
        } catch (Exception e) {
            log.error("Error deleting training with id {}", id, e);
            throw new ServiceException("Error deleting training with id " + id, e);
        }
    }

    public Optional<Training> getTrainingById(Long id) {
        try {
            return trainingDAO.findById(id);
        } catch (Exception e) {
            log.error("Error fetching training with id {}", id, e);
            throw new ServiceException("Error fetching training with id " + id, e);
        }
    }

    public List<Training> getAllTrainings() {
        try {
            Optional<List<Training>> trainings = trainingDAO.findAll();
            if (trainings.isPresent()) {
                return trainings.get();
            } else {
                log.warn("No trainings found.");
                throw new ServiceException("No trainings found.");
            }
        } catch (Exception e) {
            log.error("Error fetching all trainings", e);
            throw new ServiceException("Error fetching all trainings", e);
        }
    }

    public List<Training> getTrainingsByTraineeUsernameAndCriteria(String username,Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        try {
            Optional<List<Training>> trainings = trainingDAO.findTrainingsByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
            if (trainings.isPresent()) {
                return trainings.get();
            } else {
                log.warn("No trainings found for trainee username: {}", username);
                throw new ServiceException("No trainings found for trainee username: " + username);
            }
        } catch (Exception e) {
            log.error("Error retrieving trainings for trainee username: {}", username, e);
            throw new ServiceException("Error retrieving trainings for trainee username: " + username, e);
        }
    }

    public Optional<List<Training>> getTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName) {
        try {
            return trainingDAO.findTrainingsByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
        } catch (Exception e) {
            log.error("Error retrieving trainings for trainer username: {}", username, e);
            throw new ServiceException("Error retrieving trainings for trainer username: " + username, e);
        }
    }

}
