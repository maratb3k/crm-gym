package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Transactional
@Service
public class TrainerService {

    private TrainerDAO trainerDAO;
    private UserDAO userDAO;

    @Autowired
    public TrainerService(TrainerDAO trainerDAO, UserDAO userDAO) {
        this.trainerDAO = trainerDAO;
        this.userDAO = userDAO;
    }

    public boolean create(TrainingType specialization) {
        try {
            Trainer trainer = new Trainer(specialization);
            System.out.println(trainer.getSpecialization());
            return trainerDAO.save(trainer);
        } catch (DaoException e) {
            log.error("Error creating trainer", e);
            return false;
        }
    }

    public boolean update(String username, String password, Long id, Trainer trainer) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            return trainerDAO.update(id, trainer);
        } catch (DaoException e) {
            log.error("Error updating trainer with id {}: {}", id, e);
            return false;
        }
    }

    public boolean updatePassword(String username, String password, Long id, String newPassword) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            boolean result = trainerDAO.updatePassword(id, newPassword);
            if (!result) {
                log.error("Failed to update password for trainer with id: {}", id);
                return false;
            }
            return true;
        } catch (DaoException e) {
            log.error("Error updating password for trainer with id: {}", id, e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating password for trainer with id: {}", id, e);
            return false;
        }
    }

    public boolean updateTrainerUser(String username, String password, Long trainerId, Long userId) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }

            boolean result = trainerDAO.updateTrainerUser(trainerId, userId);
            if (!result) {
                log.error("Failed to add or update user with id " + userId + " for trainer with id: {}", trainerId);
                return false;
            }
            return true;
        } catch (DaoException e) {
            log.error("Error adding or updating user for trainer with id: {}", trainerId, e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while adding or updating user for trainer with id: {}", trainerId, e);
            return false;
        }
    }

    public boolean delete(String username, String password, Long id) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            return trainerDAO.delete(id);
        } catch (DaoException e) {
            log.error("Error deleting trainer with id {}", id, e);
            return false;
        }
    }

    public boolean deleteByUsername(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            boolean result = trainerDAO.deleteByUsername(username);
            if (!result) {
                log.error("Failed to delete trainer associated with username: {}", username);
                return false;
            }
            return true;
        } catch (DaoException e) {
            log.error("Error deleting trainer by username: {}", username, e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting trainer by username: {}", username, e);
            return false;
        }
    }

    public boolean deleteTrainerUser(String username, String password, Long trainerId) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }

            boolean result = trainerDAO.deleteTrainerUser(trainerId);
            if (!result) {
                log.error("Failed to delete user for trainer with id: {}", trainerId);
                return false;
            }
            return true;
        } catch (DaoException e) {
            log.error("Error deleting user for trainer with id: {}", trainerId, e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting user for trainer with id: {}", trainerId, e);
            return false;
        }
    }

    public Optional<Trainer> getTrainerById(String username, String password, Long id) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return trainerDAO.findById(id);
        } catch (DaoException e) {
            log.error("Error fetching trainer with id: {}", id, e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching trainer with id: {}", id, e);
            return Optional.empty();
        }
    }

    public Optional<Trainer> getTrainerByUsername(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return trainerDAO.findByUsername(username);
        } catch (DaoException e) {
            log.error("Error fetching trainer by username: {}", username, e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching trainer by username: {}", username, e);
            return Optional.empty();
        }
    }

    public Optional<List<Trainer>> getAllTrainers(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return trainerDAO.findAll();
        } catch (DaoException e) {
            log.error("Error fetching all trainers", e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching all trainers", e);
            return Optional.empty();
        }
    }

    public Optional<List<Training>> getTrainingsByTrainerUsernameAndCriteria(String username, String password, Date fromDate, Date toDate, String traineeName) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return trainerDAO.findTrainingsByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
        } catch (DaoException e) {
            log.error("Error retrieving trainings for trainer with username: {}", username, e);
            return Optional.empty();
        }
    }

    public boolean addTrainee(Long trainerId, Long traineeId) {
        try {
            return trainerDAO.addTrainee(trainerId, traineeId);
        } catch (DaoException e) {
            log.error("Error adding trainee with id {} to trainer with id {}: {}", traineeId, trainerId, e.getMessage());
            return false;
        }
    }

    public boolean addTraining(Long trainerId, Long trainingId) {
        try {
            return trainerDAO.addTraining(trainerId, trainingId);
        } catch (DaoException e) {
            log.error("Error adding training with id {} to trainer with id {}: {}", trainingId, trainerId, e.getMessage());
            return false;
        }
    }

    public boolean deleteTraineeFromList(Long trainerId, Long traineeId) {
        try {
            return trainerDAO.deleteTraineeFromList(trainerId, traineeId);
        } catch (DaoException e) {
            log.error("Error removing trainee with id {} from trainer with id {}: {}", traineeId, trainerId, e.getMessage());
            return false;
        }
    }

    public boolean deleteTrainingFromList(Long trainerId, Long trainingId) {
        try {
            return trainerDAO.deleteTrainingFromList(trainerId, trainingId);
        } catch (DaoException e) {
            log.error("Error removing training with id {} from trainer with id {}: {}", trainingId, trainerId, e.getMessage());
            return false;
        }
    }

}
