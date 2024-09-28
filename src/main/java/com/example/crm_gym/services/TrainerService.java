package com.example.crm_gym.services;

import com.example.crm_gym.dao.*;
import com.example.crm_gym.exception.*;
import com.example.crm_gym.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
public class TrainerService {

    private TrainerDAO trainerDAO;
    private TraineeDAO traineeDAO;
    private UserDAO userDAO;
    private TrainingTypeDAO trainingTypeDao;
    private TrainingDAO trainingDao;

    @Autowired
    public TrainerService(TrainerDAO trainerDAO, UserDAO userDAO, TrainingTypeDAO trainingTypeDAO, TraineeDAO traineeDAO, TrainingDAO trainingDao) {
        this.trainerDAO = trainerDAO;
        this.userDAO = userDAO;
        this.trainingTypeDao = trainingTypeDAO;
        this.traineeDAO = traineeDAO;
        this.trainingDao = trainingDao;
    }

    public Optional<Trainer> create(String firstName, String lastName, Long specializationId) {
        try {
            TrainingType trainingType = trainingTypeDao.findById(specializationId)
                    .orElseThrow(() -> new ServiceException("Training type not found"));
            User user = new User(firstName, lastName);
            Trainer trainer = new Trainer(trainingType, user);
            return trainerDAO.save(trainer);
        } catch (Exception e) {
            log.error("Error creating trainer", e);
            throw new ServiceException("Error creating trainer", e);
        }
    }

    public Optional<Trainer> update(Trainer updatedTrainer) {
        try {
            Trainer existingTrainer = trainerDAO.findById(updatedTrainer.getId())
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            User updatedUser = updatedTrainer.getUser();
            Optional<User> newUser = userDAO.update(updatedUser);
            if (newUser.isPresent()) {
                existingTrainer.setUser(newUser.get());
            }
            existingTrainer.setSpecialization(updatedTrainer.getSpecialization());
            return trainerDAO.update(existingTrainer);
        } catch (IllegalArgumentException e) {
            log.error("Invalid or empty input data for trainer update: {}", e.getMessage());
            throw new ServiceException("Invalid or empty input data for trainer update");
        } catch (Exception e) {
            log.error("Error updating trainer with id {}: {}", updatedTrainer.getId(), e);
            throw new ServiceException("Error updating trainer with id " + updatedTrainer.getId());
        }
    }

    public boolean updatePassword(Long id, String newPassword) {
        try {
            Trainer trainer = trainerDAO.findById(id)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            trainer.getUser().setPassword(newPassword);
            Optional<Trainer> result = trainerDAO.update(trainer);
            if (!result.isPresent()) {
                throw new ServiceException("Password not updated");
            }
            return true;
        } catch (Exception e) {
            log.error("Error updating password for trainer with id: {}", id, e);
            throw new ServiceException("Error updating password for trainer with id " + id);
        }
    }

    public boolean delete(Trainer trainer) {
        try {
            return trainerDAO.delete(trainer);
        } catch (Exception e) {
            log.error("Error deleting trainer with id {}", trainer.getId(), e);
            throw new ServiceException("Error deleting trainer with id " + trainer.getId());
        }
    }

    public boolean deleteByUsername(String username) {
        try {
            boolean result = trainerDAO.deleteByUsername(username);
            if (!result) {
                log.error("Failed to delete trainer associated with username: {}", username);
                throw new ServiceException("Failed to delete trainer associated with username: " + username);
            }
            return true;
        } catch (Exception e) {
            log.error("Error deleting trainer by username: {}", username, e);
            throw new ServiceException("Error deleting trainer by username: " + username);
        }
    }

    public Optional<Trainer> getTrainerById(String username, String password, Long id) {
        try {
            return trainerDAO.findById(id);
        } catch (Exception e) {
            log.error("Error fetching trainer with id: {}", id, e);
            throw new ServiceException("Error fetching trainer with id: " + id);
        }
    }

    public Optional<Trainer> getTrainerByUsername(String username) {
        try {
            return trainerDAO.findByUsername(username);
        } catch (Exception e) {
            log.error("Error fetching trainer by username: {}", username, e);
            throw new ServiceException("Error fetching trainer by username: " + username);
        }
    }

    public Optional<List<Trainer>> getAllTrainers() {
        try {
            return trainerDAO.findAll();
        } catch (Exception e) {
            log.error("Error fetching all trainers", e);
            throw new ServiceException("Error fetching all trainers");
        }
    }

    public Optional<List<Training>> getTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName) {
        try {
            return trainerDAO.findTrainingsByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
        } catch (Exception e) {
            log.error("Error retrieving trainings for trainer with username: {}", username, e);
            throw new ServiceException("Error retrieving trainings for trainer with username: " + username);
        }
    }

    public boolean addTrainee(Long trainerId, Long traineeId) {
        try {
            Trainer existingTrainer = trainerDAO.findById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            Trainee existingTrainee = traineeDAO.findById(traineeId)
                    .orElseThrow(() -> new ServiceException("Trainee not found"));
            return trainerDAO.addTrainee(existingTrainer, existingTrainee);
        } catch (Exception e) {
            log.error("Error adding trainee with id {} to trainer with id {}: {}", traineeId, trainerId, e.getMessage());
            throw new ServiceException("Error adding trainee with id " + traineeId);
        }
    }

    public boolean addTraining(Long trainerId, Long trainingId) {
        try {
            Trainer existingTrainer = trainerDAO.findById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            Training existingTraining = trainingDao.findById(trainingId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            return trainerDAO.addTraining(existingTrainer, existingTraining);
        } catch (Exception e) {
            log.error("Error adding training with id {} to trainer with id {}: {}", trainingId, trainerId, e.getMessage());
            throw new ServiceException("Error adding training with id " + trainingId);
        }
    }

    public boolean deleteTraineeFromList(Long trainerId, Long traineeId) {
        try {
            Trainee trainee = traineeDAO.findById(traineeId)
                    .orElseThrow(() -> new ServiceException("Trainee not found"));
            Trainer trainer = trainerDAO.findById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            return trainerDAO.deleteTraineeFromList(trainer, trainee);
        } catch (Exception e) {
            log.error("Error removing trainee with id {} from trainer with id {}: {}", traineeId, trainerId, e.getMessage());
            throw new ServiceException("Error removing trainee with id " + traineeId);
        }
    }

    public boolean deleteTrainingFromList(Long trainerId, Long trainingId) {
        try {
            Trainer trainer = trainerDAO.findById(trainerId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            Training training = trainingDao.findById(trainingId)
                    .orElseThrow(() -> new ServiceException("Trainer not found"));
            return trainerDAO.deleteTrainingFromList(trainer, training);
        } catch (Exception e) {
            log.error("Error removing training with id {} from trainer with id {}: {}", trainingId, trainerId, e.getMessage());
            throw new ServiceException("Error removing training with id " + trainingId);
        }
    }

}
