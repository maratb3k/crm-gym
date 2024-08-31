package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.utils.UserProfileUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    private TrainerDAO trainerDAO;

    public void createTrainer(int userId, String firstName, String lastName, String specialization) {
        logger.info("Entering createTrainer() with userId: {}, firstName: {}, lastName: {}, specialization: {}",
                userId, firstName, lastName, specialization);
        try {
            logger.info("Generating unique username for trainer with firstName: {} and lastName: {}", firstName, lastName);
            String username = generateUniqueUsername(firstName, lastName);
            logger.info("Generated unique username: {}", username);

            logger.info("Generating random password for trainer");
            String password = UserProfileUtil.generatePassword();
            logger.info("Generated random password");

            logger.info("Creating new Trainer object with username: {} and password: {}", username, password);
            Trainer trainer = new Trainer(userId, firstName, lastName, username, password, true, specialization);

            logger.info("Saving the new trainer to the database: {}", trainer);
            trainerDAO.save(trainer);
            logger.info("Trainer saved successfully with userId: {}", userId);

        } catch (Exception e) {
            logger.error("Error creating trainer with userId: {}", userId, e);
        } finally {
            logger.info("Exiting createTrainer() method");
        }
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        logger.info("Entering generateUniqueUsername() with firstName: {} and lastName: {}", firstName, lastName);

        List<Trainer> existingTrainers = trainerDAO.findAll();
        logger.info("Fetched {} existing trainers to check for username conflicts", existingTrainers.size());

        int suffix = 0;
        while (true) {
            String username = UserProfileUtil.generateUsername(firstName, lastName, suffix);

            int finalSuffix = suffix;
            if (existingTrainers.stream().noneMatch(t -> t.getUsername().equals(username))) {
                logger.info("Generated username: {}", username);
                return username;
            }
            suffix++;
        }
    }

    public void updateTrainer(int userId, Trainer trainer) {
        logger.info("Updating trainer with ID {}: {}", userId, trainer);
        try {
            trainerDAO.update(userId, trainer);
            logger.info("Trainer updated successfully: {}", trainer);
        } catch (Exception e) {
            logger.error("Error updating trainer with ID {}: {}", userId, e);
        }
    }

    public void deleteTrainer(int userId) {
        logger.info("Deleting trainer with ID {}", userId);
        try {
            trainerDAO.delete(userId);
            logger.info("Trainer deleted successfully with ID {}", userId);
        } catch (Exception e) {
            logger.error("Error deleting trainer with ID {}", userId, e);
        }
    }

    public Trainer getTrainer(int userId) {
        logger.info("Fetching trainer with ID {}", userId);
        try {
            Trainer trainer = trainerDAO.findById(userId);
            logger.info("Trainer fetched successfully: {}", trainer);
            return trainer;
        } catch (Exception e) {
            logger.error("Error fetching trainer with ID {}", userId, e);
            return null;
        }
    }

    public List<Trainer> getAllTrainers() {
        logger.info("Fetching all trainers");
        try {
            List<Trainer> trainers = trainerDAO.findAll();
            logger.info("All trainers fetched successfully, count: {}", trainers.size());
            return trainers;
        } catch (Exception e) {
            logger.error("Error fetching all trainers", e);
            return null;
        }
    }
}
