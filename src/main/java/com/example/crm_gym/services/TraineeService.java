package com.example.crm_gym.services;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Transactional
@Service
public class TraineeService {

    private TraineeDAO traineeDAO;
    private UserDAO userDAO;

    @Autowired
    public TraineeService(TraineeDAO traineeDAO, UserDAO userDAO) {
        this.traineeDAO = traineeDAO;
        this.userDAO = userDAO;
    }

    public boolean create(Date dateOfBirth, String address) {
        try {
            System.out.println("ddd");
            Trainee trainee = new Trainee(dateOfBirth, address);
            return traineeDAO.save(trainee);
        } catch (DaoException e) {
            log.error("Error creating trainee", e);
            return false;
        }
    }

    public boolean update(String username, String password, Long id, Trainee trainee) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            return traineeDAO.update(id, trainee);
        } catch (DaoException e) {
            log.error("Error updating trainee with id {}: {}", id, e);
            return false;
        }
    }

    public boolean updateTraineeUser(String username, String password, Long traineeId, Long userId) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }

            boolean result = traineeDAO.updateTraineeUser(traineeId, userId);
            if (!result) {
                log.error("Failed to add or update user with id " + userId + " for trainee with id: {}", traineeId);
                return false;
            }
            return true;
        } catch (DaoException e) {
            log.error("Error adding or updating user for trainee with id: {}", traineeId, e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while adding or updating user for trainee with id: {}", traineeId, e);
            return false;
        }
    }


    public boolean updatePassword(String username, String password, Long id, String newPassword) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }

            boolean result = traineeDAO.updatePassword(id, newPassword);
            if (!result) {
                log.error("Failed to update password for trainee with id: {}", id);
                return false;
            }
            return true;
        } catch (DaoException e) {
            log.error("Error updating password for trainee with id: {}", id, e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating password for trainee with id: {}", id, e);
            return false;
        }
    }

    public boolean delete(String username, String password, Long id) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            return traineeDAO.delete(id);
        } catch (DaoException e) {
            log.error("Error deleting trainee with id {}", id, e);
            return false;
        }
    }

    public boolean deleteByUsername(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            boolean result = traineeDAO.deleteByUsername(username);
            if (!result) {
                log.error("Failed to delete trainee associated with username: {}", username);
                return false;
            }
            return true;
        } catch (DaoException e) {
            log.error("Error deleting trainee by username: {}", username, e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting trainee by username: {}", username, e);
            return false;
        }
    }

    public boolean deleteTraineeUser(String username, String password, Long traineeId) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            boolean result = traineeDAO.deleteTraineeUser(traineeId);
            if (!result) {
                log.error("Failed to delete user for trainee with id: {}", traineeId);
                return false;
            }
            return true;
        } catch (DaoException e) {
            log.error("Error delete user for trainee with id: {}", traineeId, e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleteing user for trainee with id: {}", traineeId, e);
            return false;
        }
    }

    public Optional<Trainee> getTraineeById(String username, String password, Long id) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return traineeDAO.findById(id);
        } catch (DaoException e) {
            log.error("Error fetching trainee with id: {}", id, e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching trainee with id: {}", id, e);
            return Optional.empty();
        }
    }

    public Optional<Trainee> getTraineeByUsername(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return traineeDAO.findByUsername(username);
        } catch (DaoException e) {
            log.error("Error fetching trainee by username: {}", username, e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching trainee by username: {}", username, e);
            return Optional.empty();
        }
    }

    public Optional<List<Trainee>> getAllTrainees(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return traineeDAO.findAll();
        } catch (DaoException e) {
            log.error("Error fetching all trainees", e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching all trainees", e);
            return Optional.empty();
        }
    }

    public Optional<List<Training>> getTrainingsByTraineeUsernameAndCriteria(String username, String password, Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return traineeDAO.findTrainingsByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
        } catch (DaoException e) {
            log.error("Error retrieving trainings for trainee with username: {}", username, e);
            return Optional.empty();
        }
    }

    public Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return traineeDAO.findTrainersNotAssignedToTraineeByUsername(username);
        } catch (DaoException e) {
            log.error("Error retrieving trainers for trainee with username: {}", username, e);
            return Optional.empty();
        }
    }

    public boolean addTrainer(Long traineeId, Long trainerId) {
        try {
            return traineeDAO.addTrainer(traineeId, trainerId);
        } catch (DaoException e) {
            log.error("Error adding trainer with id {} to trainee with id {}: {}", trainerId, traineeId, e.getMessage());
            return false;
        }
    }

    public boolean addTraining(Long traineeId, Long trainingId) {
        try {
            return traineeDAO.addTraining(traineeId, trainingId);
        } catch (DaoException e) {
            log.error("Error adding training with id {} to trainee with id {}: {}", trainingId, traineeId, e.getMessage());
            return false;
        }
    }

    public boolean deleteTrainerFromList(Long traineeId, Long trainerId) {
        try {
            return traineeDAO.deleteTrainerFromList(traineeId, trainerId);
        } catch (DaoException e) {
            log.error("Error removing trainer with id {} from trainee with id {}: {}", trainerId, traineeId, e.getMessage());
            return false;
        }
    }

    public boolean deleteTrainingFromList(Long traineeId, Long trainingId) {
        try {
            return traineeDAO.deleteTrainingFromList(traineeId, trainingId);
        } catch (DaoException e) {
            log.error("Error removing training with id {} from trainee with id {}: {}", trainingId, traineeId, e.getMessage());
            return false;
        }
    }
}
