package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class TrainerDaoImpl implements TrainerDAO {

    @PersistenceContext
    private final EntityManager entityManager;
    private final UserDaoImpl userDao;
    private final TrainingDaoImpl trainingDao;

    @Autowired
    public TrainerDaoImpl(EntityManager entityManager, UserDaoImpl userDao, TrainingDaoImpl trainingDao) {
        this.entityManager = entityManager;
        this.userDao = userDao;
        this.trainingDao = trainingDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean save(Trainer trainer) {
        try {
            entityManager.persist(trainer);
        }
        catch(Exception e) {
            log.error("Error saving trainer: {}", trainer, e);
            throw new DaoException("Error saving trainer: " + trainer, e);
        }
        return true;
    }

    @Override
    public boolean addTrainee(Long trainerId, Long traineeId) {
        try {
            Trainer trainer = entityManager.find(Trainer.class, trainerId);
            if (trainer != null) {
                Trainee trainee = entityManager.find(Trainee.class, traineeId);
                if (trainee != null) {
                    trainer.getTrainees().add(trainee);
                    entityManager.merge(trainer);
                    return true;
                } else {
                    log.error("Trainee with id {} not found.", traineeId);
                    return false;
                }
            } else {
                log.error("Trainer with id {} not found.", trainerId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error adding trainee with id {} to trainer with id: {}", traineeId, trainerId, e);
            throw new DaoException("Error adding trainee with id " + traineeId + " to trainer with id " + trainerId, e);
        }
    }

    @Override
    public boolean addTraining(Long trainerId, Long trainingId) {
        try {
            Trainer trainer = entityManager.find(Trainer.class, trainerId);
            if (trainer != null) {
                Training training = entityManager.find(Training.class, trainingId);
                if (training != null) {
                    training.setTrainer(trainer);
                    trainer.getTrainings().add(training);
                    entityManager.merge(trainer);
                    return true;
                } else {
                    log.error("Training with id {} not found.", trainingId);
                    return false;
                }
            } else {
                log.error("Trainer with id {} not found.", trainerId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error adding training with id {} to trainer with id: {}", trainingId, trainerId, e);
            throw new DaoException("Error adding training with id " + trainingId + " to trainer with id " + trainerId, e);
        }
    }

    @Override
    public boolean update(Long id, Trainer updatedTrainer) {
        try {
            Trainer existingTrainer = entityManager.find(Trainer.class, id);
            if (existingTrainer != null) {
                if (updatedTrainer.getSpecialization() != null) {
                    existingTrainer.setSpecialization(updatedTrainer.getSpecialization());
                }
                if (updatedTrainer.getUser() != null) {
                    existingTrainer.setUser(updatedTrainer.getUser());
                }
                entityManager.merge(existingTrainer);
                entityManager.flush();
                return true;
            } else {
                log.error("Trainer with id {} not found.", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error updating trainer with id: {}", id, e);
            throw new DaoException("Error updating trainer with id " + id, e);
        }
    }

    @Override
    public boolean updatePassword(Long id, String newPassword) {
        try {
            Trainer trainer = entityManager.find(Trainer.class, id);
            if (trainer != null) {
                User user = trainer.getUser();
                if (user != null) {
                    userDao.updatePassword(user.getUserId(), newPassword);
                    return true;
                } else {
                    log.error("No associated user found for trainer with id {}", id);
                    return false;
                }
            } else {
                log.error("Trainer with id {} not found.", id);
                return false;
            }
        } catch (DaoException e) {
            log.error("Error updating user password for trainer with id: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating password for trainer with id: {}", id, e);
            throw new DaoException("Unexpected error updating user password with id " + id, e);
        }
    }

    @Override
    public boolean updateTrainerUser(Long trainerId, Long userId) {
        try {
            Trainer trainer = entityManager.find(Trainer.class, trainerId);
            if (trainer != null) {
                Optional<User> existingUserOptional = userDao.findById(userId);
                if (existingUserOptional.isPresent()) {
                    trainer.setUser(existingUserOptional.get());
                } else {
                    log.error("Trainer with id {} not found.", trainerId);
                    throw new DaoException("Error adding or updating user for trainer with id " + trainerId + ". User with id " + userId + " not found.");
                }
                entityManager.merge(trainer);
                entityManager.flush();
                return true;
            } else {
                log.error("Trainer with id {} not found.", trainerId);
                throw new DaoException("Error adding or updating user for trainer with id " + trainerId + " not found.");
            }
        } catch (Exception e) {
            log.error("Error adding or updating user for trainer with id: {}", trainerId, e);
            throw new DaoException("Error adding or updating user for trainer with id " + trainerId, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            String hql = "DELETE FROM Trainer t WHERE t.id = :id";
            int deletedCount = entityManager.createQuery(hql)
                    .setParameter("id", id)
                    .executeUpdate();
            if (deletedCount > 0) {
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting trainer with id: {}", id, e);
            throw new DaoException("Error deleting trainer with id " + id, e);
        }
        log.error("Error deleting trainer with id: {}", id);
        return false;
    }

    @Override
    public boolean deleteTrainerUser(Long trainerId) {
        try {
            Trainer trainer = entityManager.find(Trainer.class, trainerId);
            if (trainer != null) {
                trainer.setUser(null);
                entityManager.merge(trainer);
                entityManager.flush();
                return true;
            } else {
                log.error("Trainer with id {} not found.", trainerId);
                throw new DaoException("Error deleting user for trainer with id " + trainerId + " not found.");
            }
        } catch (Exception e) {
            log.error("Error deleting user for trainer with id: {}", trainerId, e);
            throw new DaoException("Error deleting user for trainer with id " + trainerId, e);
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
                    entityManager.remove(trainer);
                    return true;
                } else {
                    log.error("Trainer associated with user {} not found", username);
                    return false;
                }
            } else {
                log.error("User with username {} not found", username);
                return false;
            }
        } catch (DaoException e) {
            log.error("Error deleting trainer by username: {}", username, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting trainer by username: {}", username, e);
            throw new DaoException("Unexpected error deleting trainer by username " + username, e);
        }
    }

    @Override
    public boolean deleteTraineeFromList(Long trainerId, Long traineeId) {
        try {
            Trainer trainer = entityManager.find(Trainer.class, trainerId);
            if (trainer != null) {
                Trainee trainee = entityManager.find(Trainee.class, traineeId);
                if (trainee != null) {
                    if (trainer.getTrainees().remove(trainee)) {
                        entityManager.merge(trainer);
                        return true;
                    } else {
                        log.error("Trainee with id {} is not associated with trainer with id {}.", traineeId, trainerId);
                        return false;
                    }
                } else {
                    log.error("Trainee with id {} not found.", traineeId);
                    return false;
                }
            } else {
                log.error("Trainer with id {} not found.", trainerId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error removing trainee with id {} from trainer with id: {}", traineeId, trainerId, e);
            throw new DaoException("Error removing trainee with id " + traineeId + " from trainer with id " + trainerId, e);
        }
    }

    @Override
    public boolean deleteTrainingFromList(Long trainerId, Long trainingId) {
        try {
            Trainer trainer = entityManager.find(Trainer.class, trainerId);
            if (trainer != null) {
                Training training = entityManager.find(Training.class, trainingId);
                if (training != null) {
                    if (trainer.getTrainings().remove(training)) {
                        training.setTrainer(null);
                        entityManager.merge(trainer);
                        return true;
                    } else {
                        log.error("Training with id {} is not associated with trainer with id {}.", trainingId, trainerId);
                        return false;
                    }
                } else {
                    log.error("Training with id {} not found.", trainingId);
                    return false;
                }
            } else {
                log.error("Trainer with id {} not found.", trainerId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error removing training with id {} from trainer with id: {}", trainingId, trainerId, e);
            throw new DaoException("Error removing training with id " + trainingId + " from trainer with id " + trainerId, e);
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
            Optional<User> optionalUser = userDao.findByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String hql = "FROM Trainer t WHERE t.user.id = :userId";
                Trainer trainer = entityManager.createQuery(hql, Trainer.class)
                        .setParameter("userId", user.getUserId())
                        .getSingleResult();
                return Optional.ofNullable(trainer);
            } else {
                log.error("User with username " + username + " not found");
                throw new DaoException("Error finding trainer by username " + username);
            }
        } catch (Exception e) {
            log.error("Error finding trainer by username: {}", username, e);
            throw new DaoException("Error finding trainer by username " + username, e);
        }
    }

    @Override
    public Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String traineeUsername) {
        try {
            String hql = "SELECT t FROM Trainer t WHERE t.id NOT IN " +
                    "(SELECT tr.id FROM Trainee te JOIN te.trainers tr WHERE te.user.username = :username)";

            TypedQuery<Trainer> query = entityManager.createQuery(hql, Trainer.class);
            query.setParameter("username", traineeUsername);

            List<Trainer> trainers = query.getResultList();

            if (trainers.isEmpty()) {
                log.info("No trainers found not assigned to trainee with username: {}", traineeUsername);
                return Optional.empty();
            } else {
                log.info("Found {} trainers not assigned to trainee with username: {}", trainers.size(), traineeUsername);
                return Optional.of(trainers);
            }

        } catch (Exception e) {
            log.error("Error retrieving trainers not assigned to trainee with username: {}", traineeUsername, e);
            throw new DaoException("Error retrieving trainers not assigned to trainee with username: " + traineeUsername, e);
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
    public Optional<List<Training>> findTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName) {
        try {
            return trainingDao.findTrainingsByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
        } catch (DaoException e) {
            log.error("Error retrieving trainings for trainer username: {}", username, e);
            throw new DaoException("Error retrieving trainings for trainer username: " + username, e);
        }
    }

}
