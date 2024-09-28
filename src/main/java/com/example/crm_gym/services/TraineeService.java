package com.example.crm_gym.services;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.logger.TransactionLogger;
import jakarta.validation.ConstraintViolationException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
public class TraineeService extends BaseService<Trainee> {

    private final TrainerDAO trainerDAO;
    private final TraineeDAO traineeDAO;
    private final TrainingDAO trainingDAO;
    private final UserDAO userDAO;

    @Autowired
    public TraineeService(TraineeDAO traineeDAO, UserDAO userDAO, TrainerDAO trainerDAO, TrainingDAO trainingDAO) {
        super(traineeDAO);
        this.traineeDAO = traineeDAO;
        this.userDAO = userDAO;
        this.trainerDAO = trainerDAO;
        this.trainingDAO = trainingDAO;
    }

    public Optional<Trainee> create(String firstName, String lastName, Date dateOfBirth, String address, String transactionId) {
        try {
            User user = new User(firstName, lastName);
            Trainee trainee = new Trainee(dateOfBirth, address, user);
            return traineeDAO.save(trainee);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error creating trainee", transactionId, e.getMessage());
            throw e;
        }
    }

    public Optional<Trainee> update(Trainee updatedTrainee, String transactionId) {
        try {
            Optional<Trainee> trainee = traineeDAO.findByUsername(updatedTrainee.getUser().getUsername());
            if (!trainee.isPresent()) {
                log.error("[Transaction ID: {}] - Trainee not found", transactionId);
                throw new ServiceException("Trainee not found");
            }
            Trainee existingTrainee = trainee.get();
            User updatedUser = updatedTrainee.getUser();
            Optional<User> newUser = userDAO.update(updatedUser);
            if (newUser.isPresent()) {
                existingTrainee.setUser(newUser.get());
            }
            existingTrainee.setAddress(updatedTrainee.getAddress());
            existingTrainee.setDateOfBirth(updatedTrainee.getDateOfBirth());
            return traineeDAO.update(existingTrainee);
        } catch (IllegalArgumentException e) {
            log.error(" [Transaction ID: {}] - Invalid or empty input data for trainer update: {}", transactionId, e.getMessage());
            throw e;
        } catch (DaoException e) {
            log.error(" [Transaction ID: {}] - Error updating trainee with id {}: {}", transactionId, updatedTrainee.getId(), e);
            throw e;
        }
    }

    public List<Trainer> updateTraineeTrainers(String username, List<Trainer> trainers, String transactionId) {
        try {
            Optional<Trainee> optionalTrainee = traineeDAO.findByUsername(username);
            if (optionalTrainee.isPresent()) {
                Trainee trainee = optionalTrainee.get();
                trainee.setTrainers(trainers);
                Optional<Trainee> optUpdatedTrainee = traineeDAO.update(trainee);
                if(optUpdatedTrainee.isPresent()) {
                    return optUpdatedTrainee.get().getTrainers();
                } else {
                    throw new ServiceException("Error while updating trainers list for Trainee with username: " + username);
                }
            } else {
                throw new ServiceException("Trainee with username " + username + " not found");
            }
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error updating trainer list for trainee with username {}: e", transactionId, username, e);
            throw e;
        }
    }

    public boolean updateTraineeActiveStatus(String username, Boolean isActive, String transactionId) {
        try {
            Optional<Trainee> optionalTrainee = traineeDAO.findByUsername(username);
            if (optionalTrainee.isEmpty()) {
                log.error("[Transaction ID: {}] - Trainee not found: {}", transactionId, username);
                return false;
            }
            Trainee trainee = optionalTrainee.get();
            trainee.getUser().setActive(isActive);
            traineeDAO.update(trainee);
            return true;
        } catch (ConstraintViolationException e) {
            log.error("[Transaction ID: {}] - Validation failed: {}", transactionId, e.getMessage());
            throw new IllegalArgumentException("Trainee data is invalid: " + e.getConstraintViolations());
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error updating trainee with username {}: {}", transactionId, username, e);
            throw new ServiceException("Error updating trainee with username: " + username, e);
        }
    }

    public boolean delete(Trainee trainee) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return traineeDAO.delete(trainee);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error deleting trainee with id {}", transactionId, trainee.getId(), e);
            throw e;
        }
    }

    public boolean deleteByUsername(String username) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return traineeDAO.deleteByUsername(username);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error deleting trainee by username: {}", transactionId, username, e);
            throw e;
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Unexpected error occurred while deleting trainee by username: {}", transactionId, username, e);
            throw e;
        }
    }

    public Optional<Trainee> getTraineeById(Long id) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return traineeDAO.findById(id);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error occurred while fetching trainee with id: {}", transactionId, id, e);
            throw e;
        }
    }

    public Optional<Trainee> getTraineeByUsername(String username, String transactionId) {
        try {
            return traineeDAO.findByUsername(username);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error fetching trainee by username: {}", transactionId, username, e);
            throw e;
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error occurred while fetching trainee by username: {}", transactionId, username, e);
            throw e;
        }
    }

    public Optional<List<Trainee>> getAllTrainees() {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return traineeDAO.findAll();
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error fetching all trainees", transactionId, e);
            throw e;
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Unexpected error occurred while fetching all trainees", transactionId, e);
            throw e;
        }
    }

    public Optional<List<Training>> getTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName, String transactionId) {
        try {
            return traineeDAO.findTrainingsByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error retrieving trainings for trainee with username: {}", transactionId, username, e);
            throw e;
        }
    }

    public Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String username, String transactionId) {
        try {
            return traineeDAO.findTrainersNotAssignedToTraineeByUsername(username);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error retrieving trainers for trainee with username: {}", transactionId, username, e);
            throw e;
        }
    }

    public boolean addTrainer(Long traineeId, Long trainerId) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Trainee trainee = findEntityById(traineeId)
                    .orElseThrow(() -> new DaoException("Trainee not found"));
            Trainer trainer = trainerDAO.findById(trainerId)
                    .orElseThrow(() -> new DaoException("Trainer not found"));
            return traineeDAO.addTrainer(trainee, trainer);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error adding trainer with id {} to trainee with id {}: {}", transactionId, trainerId, traineeId, e.getMessage());
            throw e;
        }
    }

    public boolean addTraining(Long traineeId, Long trainingId) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Trainee trainee = findEntityById(traineeId)
                    .orElseThrow(() -> new DaoException("Trainee not found"));
            Training training = trainingDAO.findById(trainingId)
                    .orElseThrow(() -> new DaoException("Training not found"));
            return traineeDAO.addTraining(trainee, training);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error adding training with id {} to trainee with id {}: {}", transactionId, trainingId, traineeId, e.getMessage());
            throw e;
        }
    }

    public boolean deleteTrainerFromList(Long traineeId, Long trainerId) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Trainee trainee = findEntityById(traineeId)
                    .orElseThrow(() -> new DaoException("Trainee not found"));
            Trainer trainer = trainerDAO.findById(trainerId)
                    .orElseThrow(() -> new DaoException("Trainer not found"));
            return traineeDAO.deleteTrainerFromList(trainee, trainer);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error removing trainer with id {} from trainee with id {}: {}", transactionId, trainerId, traineeId, e.getMessage());
            throw e;
        }
    }

    public boolean deleteTrainingFromList(Long traineeId, Long trainingId) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Trainee trainee = findEntityById(traineeId)
                    .orElseThrow(() -> new DaoException("Trainee not found"));
            Training training = trainingDAO.findById(trainingId)
                    .orElseThrow(() -> new DaoException("Training not found"));
            return traineeDAO.deleteTrainingFromList(trainee, training);
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error removing training with id {} from trainee with id {}: {}", transactionId, trainingId, traineeId, e.getMessage());
            throw e;
        }
    }
}
