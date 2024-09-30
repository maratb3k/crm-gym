package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class TraineeDaoImpl implements TraineeDAO {

    @PersistenceContext
    private EntityManager entityManager;
    private final UserDaoImpl userDao;
    private final TrainingDaoImpl trainingDao;
    private final TrainerDaoImpl trainerDao;

    @Autowired
    @Lazy
    public TraineeDaoImpl(UserDaoImpl userDao, TrainingDaoImpl trainingDao, TrainerDaoImpl trainerDao) {
        this.userDao = userDao;
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
    }

    @Override
    public Optional<Trainee> save(Trainee trainee) {
        try {
            Optional<User> existingUser = userDao.findByFirstAndLastName(trainee.getUser().getFirstName(), trainee.getUser().getLastName());
            if (existingUser.isPresent() && existingUser.get().getTrainer() != null) {
                throw new DaoException("User already registered as a Trainer. Cannot register as a Trainee.");
            }
            boolean isUserSaved = userDao.save(trainee.getUser());
            if (!isUserSaved) {
                throw new DaoException("Error saving user: " + trainee.getUser());
            }
            entityManager.persist(trainee);
            return Optional.of(trainee);
        }
        catch(Exception e) {
            log.error("Error saving trainee: {}", trainee, e);
            throw new DaoException("Error saving trainee: " + trainee, e);
        }
    }

    @Override
    public boolean addTrainer(Trainee trainee, Trainer trainer) {
        try {
            if (trainee.getTrainers().contains(trainer)) {
                log.warn("Trainer with id {} is already associated with trainee with id {}", trainer.getId(), trainee.getId());
                throw new DaoException("Trainer with id " + trainer.getId() + " already associated with trainee with id " + trainee.getId());
            }
            trainee.getTrainers().add(trainer);
            trainer.getTrainees().add(trainee);
            entityManager.merge(trainee);
            return true;
        } catch (Exception e) {
            log.error("Error adding trainer with id {} to trainee with id: {}", trainer.getId(), trainee.getId(), e);
            throw new DaoException("Error adding trainer with id " + trainer.getId() + " to trainee with id " + trainee.getId(), e);
        }
    }

    @Override
    public boolean addTraining(Trainee trainee, Training training) {
        try {
            if (trainee.getTrainings().contains(training)) {
                log.warn("Training with id {} is already associated with trainee with id {}", training.getId(), trainee.getId());
                throw new DaoException("Training with id " + training.getId() + " is already associated with trainee with id " + trainee.getId());
            }
            training.setTrainee(trainee);
            trainee.getTrainings().add(training);
            entityManager.merge(trainee);
            return true;
        } catch (Exception e) {
            log.error("Error adding training with id {} to trainee with id: {}", training.getId(), trainee.getId(), e);
            throw new DaoException("Error adding training with id " + training.getId() + " to trainee with id " + trainee.getId(), e);
        }

    }

    public Optional<Trainee> update(Trainee updatedTrainee) {
        try {
            entityManager.merge(updatedTrainee);
            entityManager.flush();
            return Optional.of(updatedTrainee);
        } catch (ConstraintViolationException e) {
            log.error("Validation failed: {}", e.getMessage());
            throw new DaoException("User data is invalid: " + e.getConstraintViolations());
        } catch (Exception e) {
            log.error("Error updating trainee with id: {}", updatedTrainee.getId(), e);
            throw new DaoException("Error updating trainee with id " + updatedTrainee.getId(), e);
        }
    }

    @Override
    public boolean delete(Trainee trainee) {
        try {
            entityManager.remove(trainee);
            return true;
        } catch (Exception e) {
            log.error("Error deleting trainee with id: {}", trainee.getId(), e);
            throw new DaoException("Error deleting trainee with id " + trainee.getId(), e);
        }
    }

    @Override
    public boolean deleteByUsername(String username) {
        try {
            Optional<User> optionalUser = userDao.findByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Trainee trainee = entityManager.createQuery("SELECT t FROM Trainee t WHERE t.user.id = :userId", Trainee.class)
                        .setParameter("userId", user.getUserId())
                        .getSingleResult();
                if (trainee != null) {
                    entityManager.remove(trainee);
                    return true;
                } else {
                    log.error("Trainee associated with user {} not found", username);
                    throw new DaoException("Trainee associated with user " + username + " not found");
                }
            } else {
                log.error("User with username {} not found", username);
                throw new DaoException("User with username " + username + " not found");
            }
        } catch (NoResultException e) {
            log.warn("userId not found for trainee with username: {}", username, e);
            throw new DaoException("userId not found for trainee with username: " + username, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting trainee by username: {}", username, e);
            throw new DaoException("Unexpected error deleting trainee by username " + username, e);
        }
    }

    @Override
    public boolean deleteTrainerFromList(Trainee trainee, Trainer trainer) {
        try {
            if (!trainee.getTrainers().contains(trainer)) {
                log.warn("Training with id {} is not associated with trainee with id {}", trainer.getId(), trainee.getId());
                throw new DaoException("Training with id " + trainee.getId() + " is not associated with trainee with id " + trainer.getId());
            }
            trainee.getTrainers().remove(trainer);
            entityManager.merge(trainee);
            return true;
        } catch (Exception e) {
            log.error("Error removing trainer with id {} from trainee with id: {}", trainee.getId(), trainer.getId(), e);
            throw new DaoException("Error removing trainer with id " + trainer.getId() + " from trainee with id " + trainee.getId(), e);
        }
    }

    @Override
    public boolean deleteTrainingFromList(Trainee trainee, Training training) {
        try {
            if (!trainee.getTrainings().contains(training)) {
                log.warn("Training with id {} is not associated with trainee with id {}", training.getId(), trainee.getId());
                throw new DaoException("Training with id " + trainee.getId() + " is not associated with trainee with id " + training.getId());
            }
            trainee.getTrainings().remove(training);
            training.setTrainee(null);
            entityManager.merge(trainee);
            return true;
        } catch (Exception e) {
            log.error("Error removing training with id {} from trainee with id: {}", training.getId(), trainee.getId(), e);
            throw new DaoException("Error removing training with id " + training.getId() + " from trainee with id " + trainee.getId(), e);
        }
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        try {
            Trainee trainee = entityManager.find(Trainee.class, id);
            return Optional.ofNullable(trainee);
        } catch (Exception e) {
            log.error("Error finding trainee with id " + id, e);
            throw new DaoException("Error finding trainee with id " + id, e);
        }
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        try {
            Optional<User> optionalUser = userDao.findByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String hql = "FROM Trainee t WHERE t.user.id = :userId";
                Trainee trainee = entityManager.createQuery(hql, Trainee.class)
                        .setParameter("userId", user.getUserId())
                        .getSingleResult();
                return Optional.ofNullable(trainee);
            } else {
                log.error("User with username " + username + " not found");
                throw new DaoException("Error finding trainee by username " + username);
            }
        } catch (NoResultException e) {
            log.warn("User with username {} not found", username, e);
            throw new DaoException("User with username " + username + " not found", e);
        } catch (Exception e) {
            log.error("Error finding trainee by username: {}", username, e);
            throw new DaoException("Error finding trainee by username " + username, e);
        }
    }

    @Override
    public Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String traineeUsername) {
        try {
            return trainerDao.findTrainersNotAssignedToTraineeByUsername(traineeUsername);
        } catch (Exception e) {
            log.error("Error retrieving trainers not assigned to trainee with username: {}", traineeUsername, e);
            throw new DaoException("Error retrieving trainers not assigned to trainee with username: " + traineeUsername, e);
        }
    }

    @Override
    public Optional<List<Training>> findTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        try {
            return trainingDao.findTrainingsByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
        } catch (Exception e) {
            log.error("Error retrieving trainings for trainee username: {}", username, e);
            throw new DaoException("Error retrieving trainings for trainee username: " + username, e);
        }
    }

    @Override
    public Optional<List<Trainee>> findAll() {
        try {
            String hql = "FROM Trainee";
            List<Trainee> trainees = entityManager.createQuery(hql, Trainee.class).getResultList();
            return Optional.ofNullable(trainees);
        } catch (Exception e) {
            log.error("Error finding trainees", e);
            throw new DaoException("Error finding trainees", e);
        }
    }
}
