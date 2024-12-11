package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class TrainerDaoImpl implements TrainerDAO {

    private final TraineeDAO traineeDao;
    @PersistenceContext
    private EntityManager entityManager;
    private final UserDAO userDao;
    private final TrainingDAO trainingDao;

    @Autowired
    public TrainerDaoImpl(UserDAO userDao, TrainingDAO trainingDao, TraineeDAO traineeDao) {
        this.userDao = userDao;
        this.trainingDao = trainingDao;
        this.traineeDao = traineeDao;
    }

    @Override
    public Optional<Trainer> save(Trainer trainer) {
        try {
            Optional<User> existingUser = userDao.findByFirstAndLastName(trainer.getUser().getFirstName(), trainer.getUser().getLastName());
            if (existingUser.isPresent() && existingUser.get().getTrainee() != null) {
                throw new DaoException("User already registered. Cannot register as a Trainer.");
            }
            boolean isUserSaved = userDao.save(trainer.getUser());
            if (!isUserSaved) {
                throw new DaoException("Error saving user: " + trainer.getUser());
            }
            entityManager.persist(trainer);
            return Optional.of(trainer);
        }
        catch(Exception e) {
            log.error("Error saving trainer: {}", trainer, e);
            throw new DaoException("Error saving trainer: " + trainer, e);
        }
    }

    @Override
    public boolean addTrainee(Trainer trainer, Trainee trainee) {
        try {
            if (!trainer.getTrainees().contains(trainee)) {
                trainer.getTrainees().add(trainee);
                trainee.getTrainers().add(trainer);
                traineeDao.addTrainer(trainee, trainer);
                entityManager.merge(trainer);
                return true;
            } else {
                log.warn("Trainee with id {} is already associated with trainer with id {}", trainee.getId(), trainer.getId());
                return false;
            }
        } catch (Exception e) {
            log.error("Error adding trainee with id {} to trainer with id: {}", trainee.getId(), trainer.getId(), e);
            throw new DaoException("Error adding trainee with id " + trainee.getId() + " to trainer with id " + trainer.getId(), e);
        }
    }

    @Override
    public boolean addTraining(Trainer trainer, Training training) {
        try {
            if (trainer.getTrainings().contains(training)) {
                log.warn("Training with id {} is already associated with trainer with id {}", training.getId(), trainer.getId());
                throw new DaoException("Training with id " + training.getId() + " is already associated with trainer with id " + trainer.getId());
            }
            training.setTrainer(trainer);
            trainer.getTrainings().add(training);
            entityManager.merge(trainer);
            return true;

        } catch (Exception e) {
            log.error("Error adding training with id {} to trainer with id: {}", training.getId(), trainer.getId(), e);
            throw new DaoException("Error adding training with id " + training.getId() + " to trainer with id " + trainer.getId(), e);
        }
    }

    @Override
    public Optional<Trainer> update(Trainer updatedTrainer) {
        try {
            entityManager.merge(updatedTrainer);
            entityManager.flush();
            return Optional.of(updatedTrainer);
        } catch (ConstraintViolationException e) {
            log.error("Validation failed: {}", e.getMessage());
            throw new DaoException("Trainer data is invalid: " + e.getConstraintViolations());
        } catch (Exception e) {
            log.error("Error updating trainer with id: {}", updatedTrainer.getId(), e);
            throw new DaoException("Error updating trainer with id " + updatedTrainer.getId(), e);
        }
    }

    @Override
    public boolean delete(Trainer trainer) {
        try {
            Trainer attachedTrainer = entityManager.contains(trainer) ? trainer : entityManager.merge(trainer);
            entityManager.remove(attachedTrainer);
            return true;
        } catch (Exception e) {
            log.error("Error deleting trainer with id: {}", trainer.getId(), e);
            throw new DaoException("Error deleting trainer with id " + trainer.getId(), e);
        }
    }

    @Override
    public boolean deleteByUsername(String username) {
        try {
            Optional<User> optionalUser = userDao.findByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Trainer trainer = entityManager.createQuery("SELECT t FROM Trainer t WHERE t.user.id = :userId", Trainer.class)
                        .setParameter("userId", user.getUserId())
                        .getSingleResult();
                if (trainer != null) {
                    Trainer attachedTrainer = entityManager.contains(trainer) ? trainer : entityManager.merge(trainer);
                    entityManager.remove(attachedTrainer);
                    return true;
                } else {
                    log.error("Trainer associated with user {} not found", username);
                    throw new DaoException("Trainer associated with user " + username + " not found");
                }
            } else {
                log.error("User with username {} not found", username);
                throw new DaoException("User with username " + username + " not found");
            }
        } catch (NoResultException e) {
            log.warn("userId not found for trainer with username: {}", username, e);
            throw new DaoException("userId not found for trainer with username: " + username, e);
        } catch (Exception e) {
            log.error("Error occurred while deleting trainer by username: {}", username, e);
            throw new DaoException("Error deleting trainer by username " + username, e);
        }
    }

    @Override
    public Trainer deleteTrainerUser(Trainer trainer) {
        try {
            trainer.setUser(null);
            entityManager.merge(trainer);
            entityManager.flush();
            return trainer;
        } catch (Exception e) {
            log.error("Error deleting user for trainer with id: {}", trainer.getId(), e);
            throw new DaoException("Error deleting user for trainer with id " + trainer.getId(), e);
        }
    }

    @Override
    public boolean deleteTraineeFromList(Trainer trainer, Trainee trainee) {
        try {
            if (trainer.getTrainees().remove(trainee)) {
                entityManager.merge(trainer);
                return true;
            }
            log.error("Trainee with id {} is not associated with trainer with id {}.", trainee.getId(), trainer.getId());
            return false;
        } catch (Exception e) {
            log.error("Error removing trainee with id {} from trainer with id: {}", trainee.getId(), trainer.getId(), e);
            throw new DaoException("Error removing trainee with id " + trainee.getId() + " from trainer with id " + trainer.getId(), e);
        }
    }

    @Override
    public boolean deleteTrainingFromList(Trainer trainer, Training training) {
        try {
            if (trainer.getTrainings().remove(training)) {
                training.setTrainer(null);
                entityManager.merge(trainer);
                return true;
            }
            log.error("Training with id {} is not associated with trainer with id {}.", training.getId(), trainer.getId());
            return false;
        } catch (Exception e) {
            log.error("Error removing training with id {} from trainer with id: {}", training.getId(), trainer.getId(), e);
            throw new DaoException("Error removing training with id " + training.getId() + " from trainer with id " + trainer.getId(), e);
        }
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        try {
            Trainer trainer = entityManager.find(Trainer.class, id);
            return Optional.ofNullable(trainer);
        } catch (Exception e) {
            log.error("Error finding trainer with id " + id, e);
            throw new DaoException("Error finding trainer with id " + id, e);
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try {
            User user = userDao.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User with username {} not found", username);
                        return new DaoException("User with username " + username + " not found");
                    });

            String hql = "FROM Trainer t WHERE t.user.id = :userId";
            Trainer trainer = entityManager.createQuery(hql, Trainer.class)
                    .setParameter("userId", user.getUserId())
                    .getSingleResult();

            return Optional.ofNullable(trainer);
        } catch (NoResultException e) {
            log.warn("Trainer with username {} not found", username, e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error finding trainer by username: {}", username, e);
            throw new DaoException("Error finding trainer by username " + username, e);
        }
    }

    @Override
    public Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String traineeUsername) {
        try {
            String hql = "SELECT t FROM Trainer t WHERE t.id NOT IN " +
                    "(SELECT tr.id FROM Trainee te JOIN te.trainers tr WHERE te.user.username = :username)" +
                    " AND t.user.isActive = true";

            TypedQuery<Trainer> query = entityManager.createQuery(hql, Trainer.class);
            query.setParameter("username", traineeUsername);

            List<Trainer> trainers = query.getResultList();

            if (trainers.isEmpty()) {
                log.info("No active trainers found not assigned to trainee with username: {}", traineeUsername);
                return Optional.empty();
            }
            return Optional.of(trainers);
        } catch (Exception e) {
            log.error("Error retrieving active trainers not assigned to trainee with username: {}", traineeUsername, e);
            throw new DaoException("Error retrieving active trainers not assigned to trainee with username: " + traineeUsername, e);
        }
    }

    @Override
    public Optional<List<Trainer>> findAll() {
        try {
            String hql = "FROM Trainer";
            List<Trainer> trainers = entityManager.createQuery(hql, Trainer.class).getResultList();
            return Optional.ofNullable(trainers);
        } catch (Exception e) {
            log.error("Error finding trainers", e);
            throw new DaoException("Error finding trainers", e);
        }
    }

    @Override
    public List<Trainer> findTrainersByUsernames(List<String> trainerUsernames) {
        try {
            String hql = "SELECT t FROM Trainer t WHERE t.user.username IN :usernames";
            TypedQuery<Trainer> query = entityManager.createQuery(hql, Trainer.class);
            query.setParameter("usernames", trainerUsernames);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error retrieving trainers by usernames: {}", trainerUsernames, e);
            throw new DaoException("Error retrieving trainers by usernames", e);
        }
    }

    @Override
    public Optional<List<Training>> findTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName) {
        try {
            return trainingDao.findTrainingsByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
        } catch (Exception e) {
            log.error("Error retrieving trainings for trainer username: {}", username, e);
            throw new DaoException("Error retrieving trainings for trainer username: " + username, e);
        }
    }

}
