package com.example.crm_gym.services;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.utils.UserProfileUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDAO traineeDAO;

    @Autowired
    public TraineeService(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    public void createTrainee(int userId, String firstName, String lastName, Date dateOfBirth, String address) {
        logger.info("Entering createTrainee() with userId: {}, firstName: {}, lastName: {}, dateOfBirth: {}, address: {}",
                userId, firstName, lastName, dateOfBirth, address);
        try {
            logger.info("Generating unique username for trainee with firstName: {} and lastName: {}", firstName, lastName);
            String username = generateUniqueUsername(firstName, lastName);
            logger.info("Generated unique username: {}", username);

            logger.info("Generating random password for trainee");
            String password = UserProfileUtil.generatePassword();
            logger.info("Generated random password");

            logger.info("Creating new Trainee object with username: {} and password: {}", username, password);
            Trainee trainee = new Trainee(userId, firstName, lastName, username, password, true, dateOfBirth, address);

            logger.info("Saving the new trainee to the database: {}", trainee);
            traineeDAO.save(trainee);
            logger.info("Trainee saved successfully with userId: {}", userId);

        } catch (Exception e) {
            logger.error("Error creating trainee with userId: {}", userId, e);
        } finally {
            logger.info("Exiting createTrainee() method");
        }
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        logger.info("Entering generateUniqueUsername() with firstName: {} and lastName: {}", firstName, lastName);
        List<Trainee> existingTrainees = traineeDAO.findAll();
        logger.info("Fetched {} existing trainees to check for username conflicts", existingTrainees.size());

        int suffix = 0;
        while (true) {
            String username = UserProfileUtil.generateUsername(firstName, lastName, suffix);
            logger.info("Generated username: {}", username);

            if (existingTrainees.stream().noneMatch(t -> t.getUsername().equals(username))) {
                return username;
            }
            suffix++;
        }
    }

    public void updateTrainee(int userId, Trainee trainee) {
        logger.info("TraineeService - Updating trainee with ID {}: {}", userId, trainee);
        try {
            traineeDAO.update(userId, trainee);
            logger.info("TraineeService - Trainee updated successfully: {}", trainee);
        } catch (Exception e) {
            logger.error("Error updating trainee with ID {}: {}", userId, e);
        }
    }

    public void deleteTrainee(int userId) {
        logger.info("TraineeService - Deleting trainee with ID {}", userId);
        try {
            traineeDAO.delete(userId);
            logger.info("TraineeService - Trainee deleted successfully with ID {}", userId);
        } catch (Exception e) {
            logger.error("TraineeService - Error deleting trainee with ID {}", userId, e);
        }
    }

    public Trainee getTrainee(int userId) {
        logger.info("TraineeService - Fetching trainee with ID {}", userId);
        try {
            Trainee trainee = traineeDAO.findById(userId);
            logger.info("TraineeService - Trainee fetched successfully: {}", trainee);
            return trainee;
        } catch (Exception e) {
            logger.error("Error fetching trainee with ID {}", userId, e);
            return null;
        }
    }

    public List<Trainee> getAllTrainees() {
        logger.info("Fetching all trainees");
        try {
            List<Trainee> trainees = traineeDAO.findAll();
            logger.info("All trainees fetched successfully, count: {}", trainees.size());
            return trainees;
        } catch (Exception e) {
            logger.error("Error fetching all trainees", e);
            return null;
        }
    }
}