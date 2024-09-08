package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.utils.UserProfileUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    private TrainerDAO trainerDAO;

    public boolean createTrainer(int userId, String firstName, String lastName, String specialization) {
        try {
            String username = generateUniqueUsername(firstName, lastName);
            String password = UserProfileUtil.generatePassword();
            Trainer trainer = new Trainer(userId, firstName, lastName, username, password, true, specialization);
            return trainerDAO.save(trainer);
        } catch (Exception e) {
            logger.error("Error creating trainer with id: {}", userId, e);
            return false;
        }
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        Optional<List<Trainer>> optionalTrainers = trainerDAO.findAll();
        List<Trainer> existingTrainers = optionalTrainers.orElse(Collections.emptyList());
        int suffix = 0;
        while (true) {
            String username = UserProfileUtil.generateUsername(firstName, lastName, suffix);
            if (existingTrainers.stream().noneMatch(t -> t.getUsername().equals(username))) {
                return username;
            }
            suffix++;
        }
    }

    public boolean updateTrainer(int userId, Trainer trainer) {
        try {
            return trainerDAO.update(userId, trainer);
        } catch (Exception e) {
            logger.error("Error updating trainer with ID {}: {}", userId, e);
            return false;
        }
    }

    public boolean deleteTrainer(int userId) {
        try {
            return trainerDAO.delete(userId);
        } catch (Exception e) {
            logger.error("Error deleting trainer with ID {}", userId, e);
            return false;
        }
    }

    public Optional<Trainer> getTrainer(int userId) {
        try {
            return trainerDAO.findById(userId);
        } catch (DaoException e) {
            logger.error("Error fetching trainer with ID {}", userId, e);
            return Optional.empty();
        }
    }

    public List<Trainer> getAllTrainers() {
        try {
            Optional<List<Trainer>> trainers = trainerDAO.findAll();
            if (trainers.isPresent()) {
                return trainers.get();
            } else {
                logger.warn("No trainers found.");
                return Collections.emptyList();
            }
        } catch (DaoException e) {
            logger.error("Error fetching all trainers", e);
            return Collections.emptyList();
        }
    }
}
