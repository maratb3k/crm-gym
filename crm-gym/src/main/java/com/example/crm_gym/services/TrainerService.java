package com.example.crm_gym.services;

import com.example.crm_gym.dao.*;
import com.example.crm_gym.dto.TrainerDTO;
import com.example.crm_gym.dto.TrainingDTO;
import com.example.crm_gym.dtoConverter.TrainerConverter;
import com.example.crm_gym.dtoConverter.TrainingConverter;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.exception.TrainingServiceUnavailableException;
import com.example.crm_gym.logger.TransactionLogger;
import com.example.crm_gym.models.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
public class TrainerService extends BaseService<Trainer> {

    private TrainerDAO trainerDAO;
    private TraineeDAO traineeDAO;
    private TrainingDAO trainingDAO;
    private UserDAO userDAO;
    private TrainingTypeDAO trainingTypeDao;
    private TrainerConverter trainerConverter;
    private TrainingConverter trainingConverter;

    @Autowired
    @Lazy
    private TrainerTrainingsServiceClient trainerTrainingsServiceClient;

    @Autowired
    public TrainerService(TrainerDAO trainerDAO, UserDAO userDAO, TrainingTypeDAO trainingTypeDAO, TraineeDAO traineeDAO,
                          TrainingDAO trainingDAO, TrainerConverter trainerConverter, TrainingConverter trainingConverter) {
        super(trainerDAO);
        this.trainerDAO = trainerDAO;
        this.userDAO = userDAO;
        this.trainingTypeDao = trainingTypeDAO;
        this.traineeDAO = traineeDAO;
        this.trainingDAO = trainingDAO;
        this.trainerConverter = trainerConverter;
        this.trainingConverter = trainingConverter;
    }

    public Optional<Trainer> create(String firstName, String lastName, Long specializationId, String transactionId) {
        try {
            Optional<TrainingType> existingSpecialization = trainingTypeDao.findById(specializationId);
            if (!existingSpecialization.isPresent()) {
                throw new ServiceException("TrainingType does not exist");
            }
            User user = new User(firstName, lastName);
            Trainer trainer = new Trainer(existingSpecialization.get(), user);
            return trainerDAO.save(trainer);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error creating trainer", transactionId, e);
            throw new ServiceException("Error creating trainer");
        }
    }

    public Optional<TrainerDTO> update(Trainer updatedTrainer, String transactionId) {
        try {
            Optional<Trainer> trainer = trainerDAO.findByUsername(updatedTrainer.getUser().getUsername());
            if (!trainer.isPresent()) {
                log.error("[Transaction ID: {}] - Trainer not found", transactionId);
                throw new ServiceException("Trainer not found");
            }

            Trainer existingTrainer = trainer.get();
            User updatedUser = updatedTrainer.getUser();
            Optional<User> newUser = userDAO.update(updatedUser);
            if (newUser.isPresent()) {
                existingTrainer.setUser(newUser.get());
            }

            existingTrainer.setSpecialization(updatedTrainer.getSpecialization());
            Optional<Trainer> updatedTrainerEntity = trainerDAO.update(existingTrainer);

            if (updatedTrainerEntity.isPresent()) {
                return Optional.of(trainerConverter.convertToDto(updatedTrainerEntity.get()));
            } else {
                throw new ServiceException("Error while updating trainer with username: " + updatedTrainer.getUser().getUsername());
            }
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error updating trainer with id {}: {}", transactionId, updatedTrainer.getId(), e);
            throw new ServiceException("Error updating trainer with id " + updatedTrainer.getId());
        }
    }

    public boolean updateTrainerActiveStatus(String username, Boolean isActive, String transactionId) {
        try {
            Optional<Trainer> optionalTrainer = trainerDAO.findByUsername(username);
            if (optionalTrainer.isEmpty()) {
                log.error("[Transaction ID: {}] - Trainer not found: {}", transactionId, username);
                return false;
            }
            Trainer trainer = optionalTrainer.get();
            trainer.getUser().setActive(isActive);
            trainerDAO.update(trainer);
            return true;
        } catch (ConstraintViolationException e) {
            log.error("[Transaction ID: {}] - Validation failed: {}", transactionId, e.getMessage());
            throw new ServiceException("Trainer data is invalid: " + e.getConstraintViolations());
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error updating trainer with username {}: {}", transactionId, username, e);
            throw new ServiceException("Error updating trainer with username: " + username, e);
        }
    }

    public Optional<Trainer> updateTrainerUser(Long trainerId, Long userId) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Trainer trainer = findEntityById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new ServiceException("User not found"));
            trainer.setUser(user);
            return trainerDAO.update(trainer);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error adding or updating user for trainer with id: {}", transactionId, trainerId, e);
            throw new ServiceException("Error adding or updating user for trainer with id: " + trainerId, e);
        }
    }

    public boolean delete(Trainer trainer) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainerDAO.delete(trainer);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error deleting trainer with id {}", transactionId, trainer.getId(), e);
            throw new ServiceException("Error deleting trainer with id " + trainer.getId());
        }
    }

    public Trainer deleteTrainerUser(Long trainerId) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Trainer trainer = findEntityById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            return trainerDAO.deleteTrainerUser(trainer);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error deleting user for trainer with id: {}", transactionId, trainerId, e);
            throw new ServiceException("Error deleting user for trainer with id: " + trainerId, e);
        }
    }

    public Optional<Trainer> getTrainerById(Long id) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainerDAO.findById(id);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error fetching trainer with id: {}", transactionId, id, e);
           throw new ServiceException("Error fetching trainer with id: " + id);
        }
    }

    public Optional<TrainerDTO> getTrainerByUsername(String username, String transactionId) {
        try {
            Optional<Trainer> optionalTrainer = trainerDAO.findByUsername(username);
            if (optionalTrainer.isPresent()) {
                TrainerDTO trainerDTO = trainerConverter.convertToDto(optionalTrainer.get());
                return Optional.of(trainerDTO);
            } else {
                log.error("[Transaction ID: {}] - Trainer with username {} not found", transactionId, username);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error fetching trainer by username: {}", transactionId, username, e);
            throw new ServiceException("Error fetching trainer by username: " + username, e);
        }
    }

    public Optional<List<Trainer>> getAllTrainers() {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            return trainerDAO.findAll();
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error fetching all trainers", transactionId, e);
            throw new ServiceException("Error fetching all trainers");
        }
    }

    public List<Trainer> getTrainersByUsernames(List<String> trainerUsernames, String transactionId) {
        try {
            return trainerDAO.findTrainersByUsernames(trainerUsernames);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error retrieving trainers by usernames: {}", transactionId, trainerUsernames, e);
            throw new ServiceException("Error retrieving trainers by usernames: " + trainerUsernames, e);
        }
    }

    public Optional<List<TrainingDTO>> getTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName, String transactionId) {
        try {
            Optional<List<Training>> optionalTrainings = trainerDAO.findTrainingsByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
            if (!optionalTrainings.isPresent()) {
                log.error("[Transaction ID: {}] - No trainings found for trainer with username: {}", transactionId, username);
                throw new ServiceException("No trainings found for trainer with username: " + username);
            }
            List<TrainingDTO> trainingDTOs = trainingConverter.convertModelListToDtoList(optionalTrainings.get());
            return Optional.of(trainingDTOs);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error retrieving trainings for trainer with username: {}", transactionId, username, e);
            throw new ServiceException("Error retrieving trainings for trainer with username: " + username, e);
        }
    }

    public boolean addTrainee(Long trainerId, Long traineeId) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Optional<Trainer> existingTrainer = trainerDAO.findById(trainerId);
            if(existingTrainer.isEmpty()) {
                throw new ServiceException("Trainer with id " + trainerId + " not found.");
            }
            Optional<Trainee> existingTrainee = traineeDAO.findById(traineeId);
            if(existingTrainee.isEmpty()) {
                throw new ServiceException("Trainee with id " + traineeId + " not found.");
            }
            return trainerDAO.addTrainee(existingTrainer.get(), existingTrainee.get());
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error adding trainee with id {} to trainer with id {}: {}", transactionId, traineeId, trainerId, e.getMessage());
            throw new ServiceException("Error adding trainee with id " + traineeId, e);
        }
    }

    public boolean addTraining(Long trainerId, Long trainingId) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Trainer trainer = findEntityById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            Training training = trainingDAO.findById(trainingId)
                    .orElseThrow(() -> new ServiceException("Training not found"));
            return trainerDAO.addTraining(trainer, training);
//            if (result) {
//                TrainingSessionRequestDTO request = new TrainingSessionRequestDTO(
//                        trainer.getUser().getUsername(),
//                        trainer.getUser().getFirstName(),
//                        trainer.getUser().getLastName(),
//                        trainer.getUser().isActive(),
//                        convertToLocalDate(training.getTrainingDate()),
//                        training.getTrainingDuration(),
//                        "ADD"
//                );
//                trainerTrainingsServiceClient.sendTrainingSession(request);
//            }
//            return result;
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error adding training with id {} to trainer with id {}: {}", transactionId, trainingId, trainerId, e.getMessage());
            throw new ServiceException("Error adding training with id " + trainingId, e);
        }
    }

    public boolean deleteTraineeFromList(Long trainerId, Long traineeId) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Trainer trainer = findEntityById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            Trainee trainee = traineeDAO.findById(traineeId)
                    .orElseThrow(() -> new ServiceException("Trainee not found"));
            return trainerDAO.deleteTraineeFromList(trainer, trainee);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error removing trainee with id {} from trainer with id {}: {}", transactionId, traineeId, trainerId, e.getMessage());
            throw new ServiceException("Error removing trainee with id " + traineeId, e);
        }
    }

    public boolean deleteTrainingFromList(Long trainerId, Long trainingId) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Trainer trainer = findEntityById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            Training training = trainingDAO.findById(trainingId)
                    .orElseThrow(() -> new ServiceException("Training not found"));
            return trainerDAO.deleteTrainingFromList(trainer, training);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error removing training with id {} from trainer with id {}: {}", transactionId, trainingId, trainerId, e.getMessage());
            throw new ServiceException("Error removing training with id " + trainingId, e);
        }
    }

    public boolean deleteTrainerByUsername(String username, String transactionId) {
        try {
            Optional<Trainer> optionalTrainer = trainerDAO.findByUsername(username);
            if (!optionalTrainer.isPresent()) {
                TransactionLogger.logTransactionEnd(transactionId, "Delete Trainer Failed - Trainer Not Found");
                return false;
            }
            Trainer trainer = optionalTrainer.get();
            trainerDAO.delete(trainer);
            return true;
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error occurred while deleting trainer by username: {}", transactionId, username, e);
            throw new ServiceException("Error occurred while deleting trainer by username " + username, e);
        }
    }

    public double getTrainerMonthlyDuration(String username, int year, int month, String transactionId) {
        try {
            Optional<Trainer> optionalTrainer = trainerDAO.findByUsername(username);
            if (!optionalTrainer.isPresent()) {
                TransactionLogger.logTransactionEnd(transactionId, "Find Trainer's total time of training duration Failed - Trainer Not Found");
                throw new ServiceException("Find Trainer's total time of training duration Failed. Trainer Not Found");
            }
            return trainerTrainingsServiceClient.getTrainerMonthlyDuration(username, year, month, transactionId);
        } catch (TrainingServiceUnavailableException e) {
            log.error("[Transaction ID: {}] - Training Service unavailable: {}", transactionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error occurred while retrieving trainer total training duration by username: {}", transactionId, username, e);
            throw new ServiceException("Error occurred while retrieving trainer total training duration by username " + username, e);
        }
    }

}
