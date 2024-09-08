package com.example.crm_gym.facade;

import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.services.TraineeService;
import com.example.crm_gym.services.TrainerService;
import com.example.crm_gym.services.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GymCRMFacade {
    private static final Logger logger = LoggerFactory.getLogger(GymCRMFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymCRMFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public boolean createTrainee(Trainee trainee) {
        try {
            return traineeService.createTrainee(trainee.getUserId(), trainee.getFirstName(), trainee.getLastName(),
                    trainee.getDateOfBirth(), trainee.getAddress());
        } catch (Exception e) {
            logger.error("Error creating trainee: {}", trainee, e);
            return false;
        }
    }

    public Optional<Trainee> getTraineeById(int traineeId) {
        try {
            return traineeService.getTrainee(traineeId);
        } catch (Exception e) {
            logger.error("Error fetching trainee with ID {}", traineeId, e);
            return Optional.empty();
        }
    }

    public List<Trainee> getAllTrainees() {
        try {
            return traineeService.getAllTrainees();
        } catch (DaoException e) {
            logger.error("Error fetching all trainees", e);
            return Collections.emptyList();
        }
    }

    public boolean updateTrainee(int traineeId, Trainee trainee) {
        try {
            return traineeService.updateTrainee(traineeId, trainee);
        } catch (Exception e) {
            logger.error("Error updating trainee with ID {}: {}", traineeId, e);
            return false;
        }
    }

    public boolean deleteTrainee(int traineeId) {
        try {
            return traineeService.deleteTrainee(traineeId);
        } catch (Exception e) {
            logger.error("Error deleting trainee with ID {}", traineeId, e);
            return false;
        }
    }

    public boolean createTrainer(Trainer trainer) {
        try {
            return trainerService.createTrainer(trainer.getUserId(), trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization());
        } catch (Exception e) {
            logger.error("Error creating trainer: {}", trainer, e);
            return false;
        }
    }

    public Optional<Trainer> getTrainerById(int trainerId) {
        try {
            return trainerService.getTrainer(trainerId);
        } catch (Exception e) {
            logger.error("Error fetching trainer with ID {}", trainerId, e);
            return Optional.empty();
        }
    }

    public List<Trainer> getAllTrainers() {
        try {
            return trainerService.getAllTrainers();
        } catch (DaoException e) {
            logger.error("Error fetching all trainers", e);
            return Collections.emptyList();
        }
    }

    public boolean updateTrainer(int trainerId, Trainer trainer) {
        try {
            return trainerService.updateTrainer(trainerId, trainer);
        } catch (Exception e) {
            logger.error("Error updating trainer with ID {}: {}", trainerId, e);
            return false;
        }
    }

    public boolean deleteTrainer(int trainerId) {
        try {
            return trainerService.deleteTrainer(trainerId);
        } catch (Exception e) {
            logger.error("Error deleting trainer with ID {}", trainerId, e);
            return false;
        }
    }

    public boolean createTraining(Training training) {
        try {
            return trainingService.createTraining(training);
        } catch (Exception e) {
            logger.error("Error creating training: {}", training, e);
            return false;
        }
    }

    public Optional<Training> getTrainingById(int trainingId) {
        try {
            return trainingService.getTraining(trainingId);
        } catch (Exception e) {
            logger.error("Error fetching training with ID {}", trainingId, e);
            return Optional.empty();
        }
    }

    public List<Training> getAllTrainings() {
        try {
            return trainingService.getAllTrainings();
        } catch (DaoException e) {
            logger.error("Error fetching all trainings", e);
            return Collections.emptyList();
        }
    }

    public boolean updateTraining(int id, Training training) {
        try {
            return trainingService.updateTraining(id, training);
        } catch (Exception e) {
            logger.error("Error updating training with ID {}: {}", id, e);
            return false;
        }
    }

    public boolean deleteTraining(int trainingId) {
        try {
            return trainingService.deleteTraining(trainingId);
        } catch (Exception e) {
            logger.error("Error deleting training with ID {}", trainingId, e);
            return false;
        }
    }
}
