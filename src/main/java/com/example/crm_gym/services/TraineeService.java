package com.example.crm_gym.services;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.utils.UserProfileUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Slf4j
@Service
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDAO traineeDAO;

    @Autowired
    public TraineeService(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    public boolean createTrainee(int userId, String firstName, String lastName, Date dateOfBirth, String address) {
        try {
            String username = generateUniqueUsername(firstName, lastName);
            String password = UserProfileUtil.generatePassword();
            Trainee trainee = new Trainee(userId, firstName, lastName, username, password, true, dateOfBirth, address);
            return traineeDAO.save(trainee);
        } catch (Exception e) {
            logger.error("Error creating trainee with id: {}", userId, e);
            return false;
        }
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        Optional<List<Trainee>> optionalTrainees = traineeDAO.findAll();
        List<Trainee> existingTrainees = optionalTrainees.orElse(Collections.emptyList());
        int suffix = 0;
        while (true) {
            String username = UserProfileUtil.generateUsername(firstName, lastName, suffix);
            if (existingTrainees.stream().noneMatch(t -> t.getUsername().equals(username))) {
                return username;
            }
            suffix++;
        }
    }

    public boolean updateTrainee(int userId, Trainee trainee) {
        try {
            return traineeDAO.update(userId, trainee);
        } catch (Exception e) {
            logger.error("Error updating trainee with ID {}: {}", userId, e);
            return false;
        }
    }

    public boolean deleteTrainee(int userId) {
        try {
            return traineeDAO.delete(userId);
        } catch (Exception e) {
            logger.error("TraineeService - Error deleting trainee with ID {}", userId, e);
            return false;
        }
    }

    public Optional<Trainee> getTrainee(int userId) {
        try {
            return traineeDAO.findById(userId);
        } catch (DaoException e) {
            logger.error("Error fetching trainee with ID {}", userId, e);
            return Optional.empty();
        }
    }

    public List<Trainee> getAllTrainees() {
        try {
            Optional<List<Trainee>> trainees = traineeDAO.findAll();
            if (trainees.isPresent()) {
                return trainees.get();
            } else {
                logger.warn("No trainees found.");
                return Collections.emptyList();
            }
        } catch (DaoException e) {
            logger.error("Error fetching all trainees", e);
            return Collections.emptyList();
        }
    }
}