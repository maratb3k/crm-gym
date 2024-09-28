package com.example.crm_gym.services;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.*;
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

@Slf4j
@Transactional
@Service
public class TraineeService {

    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;
    private UserDAO userDAO;
    private TrainingDAO trainingDAO;

    @Autowired
    public TraineeService(TraineeDAO traineeDAO, UserDAO userDAO, TrainerDAO trainerDAO, TrainingDAO trainingDAO) {
        this.traineeDAO = traineeDAO;
        this.userDAO = userDAO;
        this.trainerDAO = trainerDAO;
        this.trainingDAO = trainingDAO;
    }

    public Optional<Trainee> create(String firstName, String lastName, Date dateOfBirth, String address) {
        try {
            User user = new User(firstName, lastName);
            Trainee trainee = new Trainee(dateOfBirth, address, user);
            return traineeDAO.save(trainee);
        } catch (Exception e) {
            log.error("Error creating trainee", e.getMessage());
            throw new ServiceException("Error creating trainee");
        }
    }

    public Optional<Trainee> update(Trainee updatedTrainee) {
        try {
            Trainee existingTrainee = traineeDAO.findByUsername(updatedTrainee.getUser().getUsername())
                    .orElseThrow(() -> new ServiceException("Trainee not found"));
            User updatedUser = updatedTrainee.getUser();
            Optional<User> newUser = userDAO.update(updatedUser);
            if (newUser.isPresent()) {
                existingTrainee.setUser(newUser.get());
            }
            existingTrainee.setAddress(updatedTrainee.getAddress());
            existingTrainee.setDateOfBirth(updatedTrainee.getDateOfBirth());
            return traineeDAO.update(existingTrainee);
        } catch (IllegalArgumentException e) {
            log.error("Invalid or empty input data for trainer update: {}", e.getMessage());
            throw new ServiceException("Invalid or empty input data for trainer update");
        } catch (Exception e) {
            log.error("Error updating trainee: {}", e.getMessage());
            throw new ServiceException("Error updating trainee");
        }
    }

    public boolean updatePassword(String username, Long id, String newPassword) {
        try {
            Trainee existingTrainee = traineeDAO.findByUsername(username)
                    .orElseThrow(() -> new ServiceException("Trainee not found"));
            User user = existingTrainee.getUser();
            user.setPassword(newPassword);
            traineeDAO.update(existingTrainee);
            return true;
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating password for trainee with id: {}", id, e);
            throw new ServiceException("Unexpected error occurred while updating password for trainee with id: " + id);
        }
    }

    public boolean delete(Trainee trainee) {
        try {
            return traineeDAO.delete(trainee);
        } catch (Exception e) {
            log.error("Error deleting trainee with id {}", trainee.getId(), e);
            throw new ServiceException("Error deleting trainee with id " + trainee.getId());
        }
    }

    public boolean deleteByUsername(String username) {
        try {
            boolean result = traineeDAO.deleteByUsername(username);
            if (!result) {
                log.error("Failed to delete trainee associated with username: {}", username);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting trainee by username: {}", username, e);
            throw new ServiceException("Unexpected error occurred while deleting trainee by username");
        }
    }

    public Optional<Trainee> getTraineeById(Long id) {
        try {
            return traineeDAO.findById(id);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching trainee with id: {}", id, e);
            throw new ServiceException("Error occurred while fetching trainee with id: " + id);
        }
    }

    public Optional<Trainee> getTraineeByUsername(String username) {
        try {
            return traineeDAO.findByUsername(username);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching trainee by username: {}", username, e);
            throw new ServiceException("Error occurred while fetching trainee by username");
        }
    }

    public Optional<List<Trainee>> getAllTrainees() {
        try {
            return traineeDAO.findAll();
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching all trainees", e);
            throw new ServiceException("Error occurred while fetching all trainees");
        }
    }

    public Optional<List<Training>> getTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        try {
            return traineeDAO.findTrainingsByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
        } catch (Exception e) {
            log.error("Error retrieving trainings for trainee with username: {}", username, e);
            throw new ServiceException("Error retrieving trainings for trainee with username: " + username);
        }
    }

    public Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String username) {
        try {
            return traineeDAO.findTrainersNotAssignedToTraineeByUsername(username);
        } catch (Exception e) {
            log.error("Error retrieving trainers for trainee with username: {}", username, e);
            throw new ServiceException("Error retrieving trainers for trainee with username: " + username);
        }
    }

    public boolean addTrainer(Long traineeId, Long trainerId) {
        try {
            Trainee trainee = traineeDAO.findById(traineeId)
                    .orElseThrow(() -> new ServiceException("Trainee not found"));
            Trainer trainer = trainerDAO.findById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            return traineeDAO.addTrainer(trainee, trainer);
        } catch (Exception e) {
            log.error("Error adding trainer with id {} to trainee with id {}: {}", trainerId, traineeId, e.getMessage());
            throw new ServiceException("Error adding trainer with id " + trainerId);
        }
    }

    public boolean addTraining(Long traineeId, Long trainingId) {
        try {
            Trainee trainee = traineeDAO.findById(traineeId)
                    .orElseThrow(() -> new ServiceException("Trainee not found"));
            Training training = trainingDAO.findById(trainingId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            return traineeDAO.addTraining(trainee, training);
        } catch (Exception e) {
            log.error("Error adding training with id {} to trainee with id {}: {}", trainingId, traineeId, e.getMessage());
            throw new ServiceException("Error adding training with id " + trainingId);
        }
    }

    public boolean deleteTrainerFromList(Long traineeId, Long trainerId) {
        try {
            Trainee trainee = traineeDAO.findById(traineeId)
                    .orElseThrow(() -> new ServiceException("Trainee not found"));
            Trainer trainer = trainerDAO.findById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            return traineeDAO.deleteTrainerFromList(trainee, trainer);
        } catch (Exception e) {
            log.error("Error removing trainer with id {} from trainee with id {}: {}", trainerId, traineeId, e.getMessage());
            throw new ServiceException("Error removing trainer with id " + trainerId);
        }
    }

    public boolean deleteTrainingFromList(Long traineeId, Long trainingId) {
        try {
            Trainee trainee = traineeDAO.findById(traineeId)
                    .orElseThrow(() -> new ServiceException("Trainee not found"));
            Training training = trainingDAO.findById(trainingId)
                    .orElseThrow(() -> new ServiceException("Training not found"));
            return traineeDAO.deleteTrainingFromList(trainee, training);
        } catch (Exception e) {
            log.error("Error removing training with id {} from trainee with id {}: {}", trainingId, traineeId, e.getMessage());
            throw new ServiceException("Error removing training with id " + trainingId);
        }
    }
}
