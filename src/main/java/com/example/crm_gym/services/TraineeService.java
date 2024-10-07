package com.example.crm_gym.services;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.dto.*;
import com.example.crm_gym.dtoConverter.TraineeConverter;
import com.example.crm_gym.dtoConverter.TrainerConverter;
import com.example.crm_gym.dtoConverter.TrainingConverter;
import com.example.crm_gym.exception.*;
import com.example.crm_gym.logger.TransactionLogger;
import jakarta.validation.ConstraintViolationException;
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
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class TraineeService extends BaseService<Trainee> {

    private final TrainerDAO trainerDAO;
    private final TraineeDAO traineeDAO;
    private final TrainingDAO trainingDAO;
    private final UserDAO userDAO;
    private final TraineeConverter traineeConverter;
    private final TrainingConverter trainingConverter;
    private final TrainerConverter trainerConverter;

    @Autowired
    public TraineeService(TraineeDAO traineeDAO, UserDAO userDAO, TrainerDAO trainerDAO, TrainingDAO trainingDAO,
                          TraineeConverter traineeConverter, TrainingConverter trainingConverter, TrainerConverter trainerConverter) {
        super(traineeDAO);
        this.traineeDAO = traineeDAO;
        this.userDAO = userDAO;
        this.trainerDAO = trainerDAO;
        this.trainingDAO = trainingDAO;
        this.traineeConverter = traineeConverter;
        this.trainingConverter = trainingConverter;
        this.trainerConverter = trainerConverter;
    }

    public Optional<Trainee> create(String firstName, String lastName, Date dateOfBirth, String address, String transactionId) {
        try {
            log.info("[Transaction ID: {}] - Creating new Trainee with firstName: {}, lastName: {}", transactionId, firstName, lastName);

            User user = new User(firstName, lastName);
            Trainee trainee = new Trainee(dateOfBirth, address, user);
            Optional<Trainee> savedTrainee = traineeDAO.save(trainee);
            log.info("[Transaction ID: {}] - Successfully created Trainee: {}", transactionId, savedTrainee.orElse(null));
            return savedTrainee;
        } catch (DaoException e) {
            log.error("[Transaction ID: {}] - Error creating trainee: {}", transactionId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - General error creating trainee: {}", transactionId, e.getMessage(), e);
            throw new ServiceException("[Transaction ID: " + transactionId + "] - Error creating trainee", e);
        }
    }


    public Optional<TraineeDTO> update(String username, String firstName, String lastName,
                                    Date dateOfBirth, String address, Boolean isActive,
                                    String transactionId) {
        try {
            Optional<Trainee> optionalTrainee = traineeDAO.findByUsername(username);
            if (!optionalTrainee.isPresent()) {
                log.error("[Transaction ID: {}] - Trainee not found", transactionId);
                throw new ServiceException("Trainee not found with username " + username);
            }

            Trainee existingTrainee = optionalTrainee.get();

            User existingUser = existingTrainee.getUser();
            existingUser.setFirstName(firstName);
            existingUser.setLastName(lastName);
            existingUser.setActive(isActive);

            Optional<User> updatedUser = userDAO.update(existingUser);
            if (updatedUser.isPresent()) {
                existingTrainee.setUser(updatedUser.get());
            }
            existingTrainee.setAddress(address);
            existingTrainee.setDateOfBirth(dateOfBirth);

            traineeDAO.update(existingTrainee);
            return Optional.of(traineeConverter.convertToDto(existingTrainee));
        } catch (IllegalArgumentException e) {
            log.error(" [Transaction ID: {}] - Invalid or empty input data for trainer update: {}", transactionId, e.getMessage());
            throw new ServiceException("Invalid or empty input data for trainer update");
        } catch (Exception e) {
            log.error(" [Transaction ID: {}] - Error updating trainee with username {}: {}", transactionId, username, e);
            throw new ServiceException("Error updating trainee with username " + username);
        }
    }

    public List<TrainerDTO> updateTraineeTrainers(String username, List<Trainer> trainers, String transactionId) {
        try {
            Optional<Trainee> optionalTrainee = traineeDAO.findByUsername(username);
            if (optionalTrainee.isPresent()) {
                Trainee trainee = optionalTrainee.get();
                trainee.setTrainers(trainers);

                Optional<Trainee> optUpdatedTrainee = traineeDAO.update(trainee);
                if (optUpdatedTrainee.isPresent()) {
                    List<Trainer> updatedTrainers = optUpdatedTrainee.get().getTrainers();
                    return trainerConverter.convertModelListToDtoList(updatedTrainers);
                } else {
                    throw new ServiceException("Error while updating trainers list for Trainee with username: " + username);
                }
            } else {
                throw new ServiceException("Trainee with username " + username + " not found");
            }
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error updating trainer list for trainee with username {}: e", transactionId, username, e);
            throw new ServiceException("Error while updating trainer list for trainee with username " + username);
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
            throw new ServiceException("Trainee data is invalid: " + e.getConstraintViolations());
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error updating trainee with username {}: {}", transactionId, username, e);
            throw new ServiceException("Error updating trainee with username: " + username, e);
        }
    }

    public boolean deleteTraineeByUsername(String username, String transactionId) {
        try {
            Optional<Trainee> optionalTrainee = traineeDAO.findByUsername(username);
            if (!optionalTrainee.isPresent()) {
                TransactionLogger.logTransactionEnd(transactionId, "Delete Trainee Failed - Trainee Not Found");
                return false;
            }
            Trainee trainee = optionalTrainee.get();
            traineeDAO.delete(trainee);
            return true;
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error deleting trainee with username {}", transactionId, username, e);
            TransactionLogger.logTransactionEnd(transactionId, "Delete Trainee Failed - Exception Occurred");
            throw new ServiceException("Error deleting trainee with username " + username, e);
        }
    }

    public boolean deleteByUsername(String username) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return traineeDAO.deleteByUsername(username);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error occurred while deleting trainee by username: {}", transactionId, username, e);
            throw new ServiceException("Error occurred while deleting trainee by username");
        }
    }

    public Optional<Trainee> getTraineeById(Long id) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return traineeDAO.findById(id);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error occurred while fetching trainee with id: {}", transactionId, id, e);
            throw new ServiceException("Error occurred while fetching trainee with id " + id);
        }
    }

    public Optional<TraineeDTO> getTraineeByUsername(String username, String transactionId) {
        try {
            Optional<Trainee> optionalTrainee =  traineeDAO.findByUsername(username);
            if(!optionalTrainee.isPresent()) {
                log.error("[Transaction ID: {}] - Trainee not found: {}", transactionId, username);
                throw new ServiceException("Trainee with username " + username + " not found");
            }
            Trainee trainee = optionalTrainee.get();
            TraineeDTO traineeDTO = traineeConverter.convertToDto(trainee);
            return Optional.ofNullable(traineeDTO);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error occurred while fetching trainee by username: {}", transactionId, username, e);
            throw new ServiceException("Error occurred while fetching trainee by username: " + username);
        }
    }

    public Optional<List<Trainee>> getAllTrainees() {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return traineeDAO.findAll();
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Unexpected error occurred while fetching all trainees", transactionId, e);
            throw new ServiceException("Error occurred while fetching all trainees");
        }
    }

    public Optional<List<TrainingDTO>> getTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate,
                                                                             Date toDate, String trainerName,
                                                                             String trainingTypeName, String transactionId) {
        try {
            Optional<List<Training>> optionalTrainings = traineeDAO.findTrainingsByTraineeUsernameAndCriteria(
                    username, fromDate, toDate, trainerName, trainingTypeName);

            if (!optionalTrainings.isPresent() || optionalTrainings.get().isEmpty()) {
                TransactionLogger.logTransactionEnd(transactionId, "Get Trainee Trainings Failed - No Trainings Found");
                return Optional.empty();
            }
            List<TrainingDTO> trainingDTOs = trainingConverter.convertModelListToDtoList(optionalTrainings.get());
            return Optional.of(trainingDTOs);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error retrieving trainings for trainee with username: {}", transactionId, username, e);
            throw new ServiceException("Error retrieving trainings for trainee with username: " + username);
        }
    }

    public Optional<List<TrainerDTO>> findTrainersNotAssignedToTraineeByUsername(String username, Boolean isActive, String transactionId) {
        try {
            Optional<List<Trainer>> optionalTrainers = traineeDAO.findTrainersNotAssignedToTraineeByUsername(username);

            if (!optionalTrainers.isPresent() || optionalTrainers.get().isEmpty()) {
                log.error("[Transaction ID: {}] - No trainers found for trainee with username: {}", transactionId, username);
                return Optional.empty();
            }

            List<Trainer> filteredTrainers = optionalTrainers.get().stream()
                    .filter(trainer -> trainer.getUser().isActive() == isActive)
                    .collect(Collectors.toList());

            List<TrainerDTO> trainerDTOs = trainerConverter.convertModelListToDtoList(filteredTrainers);
            return Optional.of(trainerDTOs);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error retrieving trainers for trainee with username: {}", transactionId, username, e);
            throw new ServiceException("Error retrieving trainers for trainee with username: " + username);
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
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error adding trainer with id {} to trainee with id {}: {}", transactionId, trainerId, traineeId, e.getMessage());
            throw new ServiceException("Error adding trainer with id " + trainerId);
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
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error adding training with id {} to trainee with id {}: {}", transactionId, trainingId, traineeId, e.getMessage());
            throw new ServiceException("Error adding training with id " + trainingId);
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
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error removing trainer with id {} from trainee with id {}: {}", transactionId, trainerId, traineeId, e.getMessage());
            throw new ServiceException("Error removing trainer with id " + trainerId);
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
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error removing training with id {} from trainee with id {}: {}", transactionId, trainingId, traineeId, e.getMessage());
            throw new ServiceException("Error removing training with id " + trainingId);
        }
    }
}
