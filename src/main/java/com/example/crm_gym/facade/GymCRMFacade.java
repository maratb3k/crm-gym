package com.example.crm_gym.facade;

import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.services.TraineeService;
import com.example.crm_gym.services.TrainerService;
import com.example.crm_gym.services.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public void createTrainee(Trainee trainee) {
        logger.info("Creating trainee: {}", trainee);
        try {
            traineeService.createTrainee(trainee.getUserId(), trainee.getFirstName(), trainee.getLastName(),
                    trainee.getDateOfBirth(), trainee.getAddress());
            logger.info("Trainee created successfully: {}", trainee);
        } catch (Exception e) {
            logger.error("Error creating trainee: {}", trainee, e);
        }
    }

    public Trainee getTraineeById(int traineeId) {
        logger.info("Fetching trainee with ID {}", traineeId);
        try {
            Trainee trainee = traineeService.getTrainee(traineeId);
            logger.info("Trainee fetched successfully: {}", trainee);
            return trainee;
        } catch (Exception e) {
            logger.error("Error fetching trainee with ID {}", traineeId, e);
            return null;
        }
    }

    public List<Trainee> getAllTrainees() {
        logger.info("Fetching all trainees");
        try {
            List<Trainee> trainees = traineeService.getAllTrainees();
            logger.info("All trainees fetched successfully, count: {}", trainees.size());
            return trainees;
        } catch (Exception e) {
            logger.error("Error fetching all trainees", e);
            return null;
        }
    }

    public void updateTrainee(int traineeId, Trainee trainee) {
        logger.info("Updating trainee with ID {}: {}", traineeId, trainee);
        try {
            traineeService.updateTrainee(traineeId, trainee);
            logger.info("Trainee updated successfully: {}", trainee);
        } catch (Exception e) {
            logger.error("Error updating trainee with ID {}: {}", traineeId, e);
        }
    }

    public void deleteTrainee(int traineeId) {
        logger.info("Deleting trainee with ID {}", traineeId);
        try {
            traineeService.deleteTrainee(traineeId);
            logger.info("Trainee deleted successfully with ID {}", traineeId);
        } catch (Exception e) {
            logger.error("Error deleting trainee with ID {}", traineeId, e);
        }
    }

    public void createTrainer(Trainer trainer) {
        logger.info("Creating trainer: {}", trainer);
        try {
            trainerService.createTrainer(trainer.getUserId(), trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization());
            logger.info("Trainer created successfully: {}", trainer);
        } catch (Exception e) {
            logger.error("Error creating trainer: {}", trainer, e);
        }
    }

    public Trainer getTrainerById(int trainerId) {
        logger.info("Fetching trainer with ID {}", trainerId);
        try {
            Trainer trainer = trainerService.getTrainer(trainerId);
            logger.info("Trainer fetched successfully: {}", trainer);
            return trainer;
        } catch (Exception e) {
            logger.error("Error fetching trainer with ID {}", trainerId, e);
            return null;
        }
    }

    public List<Trainer> getAllTrainers() {
        logger.info("Fetching all trainers");
        try {
            List<Trainer> trainers = trainerService.getAllTrainers();
            logger.info("All trainers fetched successfully, count: {}", trainers.size());
            return trainers;
        } catch (Exception e) {
            logger.error("Error fetching all trainers", e);
            return null;
        }
    }

    public void updateTrainer(int trainerId, Trainer trainer) {
        logger.info("Updating trainer with ID {}: {}", trainerId, trainer);
        try {
            trainerService.updateTrainer(trainerId, trainer);
            logger.info("Trainer updated successfully: {}", trainer);
        } catch (Exception e) {
            logger.error("Error updating trainer with ID {}: {}", trainerId, e);
        }
    }

    public void deleteTrainer(int trainerId) {
        logger.info("Deleting trainer with ID {}", trainerId);
        try {
            trainerService.deleteTrainer(trainerId);
            logger.info("Trainer deleted successfully with ID {}", trainerId);
        } catch (Exception e) {
            logger.error("Error deleting trainer with ID {}", trainerId, e);
        }
    }

    public void createTraining(Training training) {
        logger.info("Creating training: {}", training);
        try {
            trainingService.createTraining(training);
            logger.info("Training created successfully: {}", training);
        } catch (Exception e) {
            logger.error("Error creating training: {}", training, e);
        }
    }

    public Training getTrainingById(int trainingId) {
        logger.info("Fetching training with ID {}", trainingId);
        try {
            Training training = trainingService.getTraining(trainingId);
            logger.info("Training fetched successfully: {}", training);
            return training;
        } catch (Exception e) {
            logger.error("Error fetching training with ID {}", trainingId, e);
            return null;
        }
    }

    public List<Training> getAllTrainings() {
        logger.info("Fetching all trainings");
        try {
            List<Training> trainings = trainingService.getAllTrainings();
            logger.info("All trainings fetched successfully, count: {}", trainings.size());
            return trainings;
        } catch (Exception e) {
            logger.error("Error fetching all trainings", e);
            return null;
        }
    }

    public void updateTraining(int id, Training training) {
        logger.info("Updating training with ID {}: {}", id, training);
        try {
            trainingService.updateTraining(id, training);
            logger.info("Training updated successfully: {}", training);
        } catch (Exception e) {
            logger.error("Error updating training with ID {}: {}", id, e);
        }
    }

    public void deleteTraining(int trainingId) {
        logger.info("Deleting training with ID {}", trainingId);
        try {
            trainingService.deleteTraining(trainingId);
            logger.info("Training deleted successfully with ID {}", trainingId);
        } catch (Exception e) {
            logger.error("Error deleting training with ID {}", trainingId, e);
        }
    }
}
