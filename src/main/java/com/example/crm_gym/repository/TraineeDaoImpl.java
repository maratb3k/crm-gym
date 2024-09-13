package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class TraineeDaoImpl implements TraineeDAO {

    @PersistenceContext
    private final EntityManager entityManager;
    private final UserDaoImpl userDao;
    private final TrainingDaoImpl trainingDao;
    private final TrainerDaoImpl trainerDao;

    public TraineeDaoImpl(EntityManager entityManager, UserDaoImpl userDao, TrainingDaoImpl trainingDao, TrainerDaoImpl trainerDao) {
        this.entityManager = entityManager;
        this.userDao = userDao;
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
    }

    @Override
    public boolean save(Trainee trainee) {
        try {
            System.out.println("rep");
            entityManager.persist(trainee);
            return true;
        }
        catch(Exception e) {
            System.out.println(e);
            log.error("Error saving trainee: {}", trainee, e);
            throw new DaoException("Error saving trainee: " + trainee, e);
        }
    }

    @Override
    public boolean addTrainer(Long traineeId, Long trainerId) {
        try {
            Trainee trainee = entityManager.find(Trainee.class, traineeId);
            if (trainee != null) {
                Trainer trainer = entityManager.find(Trainer.class, trainerId);
                if (trainer != null) {
                    trainee.getTrainers().add(trainer);
                    entityManager.merge(trainee);
                    return true;
                } else {
                    log.error("Trainer with id {} not found.", trainerId);
                    return false;
                }
            } else {
                log.error("Trainee with id {} not found.", traineeId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error adding trainer with id {} to trainee with id: {}", trainerId, traineeId, e);
            throw new DaoException("Error adding trainer with id " + trainerId + " to trainee with id " + traineeId, e);
        }
    }

    @Override
    public boolean addTraining(Long traineeId, Long trainingId) {
        try {
            Trainee trainee = entityManager.find(Trainee.class, traineeId);
            if (trainee != null) {
                Training training = entityManager.find(Training.class, trainingId);
                if (training != null) {
                    training.setTrainee(trainee);
                    trainee.getTrainings().add(training);
                    entityManager.merge(trainee);
                    return true;
                } else {
                    log.error("Training with id {} not found.", trainingId);
                    return false;
                }
            } else {
                log.error("Trainee with id {} not found.", traineeId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error adding training with id {} to trainee with id: {}", trainingId, traineeId, e);
            throw new DaoException("Error adding training with id " + trainingId + " to trainee with id " + traineeId, e);
        }

    }

    @Override
    public boolean update(Long id, Trainee updatedTrainee) {
        try {
            Trainee existingTrainee = entityManager.find(Trainee.class, id);
            if(existingTrainee != null) {
                if(updatedTrainee.getDateOfBirth() != null) {
                    existingTrainee.setDateOfBirth(updatedTrainee.getDateOfBirth());
                } else if(StringUtils.hasText(updatedTrainee.getAddress())) {
                    existingTrainee.setAddress(updatedTrainee.getAddress());
                } else if(updatedTrainee.getUser() != null) {
                    existingTrainee.setUser(updatedTrainee.getUser());
                } else if(updatedTrainee.getTrainers() != null && !updatedTrainee.getTrainers().isEmpty()) {
                    existingTrainee.setTrainers(updatedTrainee.getTrainers());
                } else if(updatedTrainee.getTrainings() != null && !updatedTrainee.getTrainings().isEmpty()) {
                    existingTrainee.setTrainings(updatedTrainee.getTrainings());
                }
                entityManager.merge(existingTrainee);
                entityManager.flush();
                return true;
            } else {
                log.error("Trainee with id {} not found.", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error updating trainee with id: {}", id, e);
            throw new DaoException("Error updating trainee with id " + id, e);
        }
    }

    @Override
    public boolean updatePassword(Long id, String newPassword) {
        try {
            Trainee trainee = entityManager.find(Trainee.class, id);
            if (trainee != null) {
                User user = trainee.getUser();
                if (user != null) {
                    userDao.updatePassword(user.getUserId(), newPassword);
                    return true;
                } else {
                    log.error("No associated user found for trainee with id {}", id);
                    return false;
                }
            } else {
                log.error("Trainee with id {} not found.", id);
                return false;
            }
        } catch (DaoException e) {
            log.error("Error updating user password for trainee with id: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating password for trainee with id: {}", id, e);
            throw new DaoException("Unexpected error updating user password for trainee with id " + id, e);
        }
    }

    @Override
    public boolean updateTrainersList(Long id, Set<Trainer> trainers) {
        try {
            Trainee trainee = entityManager.find(Trainee.class, id);
            if (trainee != null) {
                trainee.setTrainers(trainers);
                entityManager.merge(trainee);
                return true;
            } else {
                log.error("Error updating trainer list. Trainee with id: {} not found.", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error updating trainer list. Trainee with id: {} not found.", id);
            throw new DaoException("Error updating trainer list. Trainee with id " + id + " not found.", e);
        }
    }

    @Override
    public boolean updateTraineeUser(Long traineeId, Long userId) {
        try {
            Trainee trainee = entityManager.find(Trainee.class, traineeId);

            if (trainee != null) {
                Optional<User> existingUserOptional = userDao.findById(userId);
                if (existingUserOptional.isPresent()) {
                    trainee.setUser(existingUserOptional.get());
                } else {
                    log.error("Trainee with id {} not found.", traineeId);
                    throw new DaoException("Error adding or updating user for trainee with id " + traineeId + ". User with id " + userId + " not found.");
                }
                entityManager.merge(trainee);
                entityManager.flush();
                return true;
            } else {
                log.error("Trainee with id {} not found.", traineeId);
                throw new DaoException("Error adding or updating user for trainee with id " + traineeId + " not found.");
            }
        } catch (Exception e) {
            log.error("Error adding or updating user for trainee with id: {}", traineeId, e);
            throw new DaoException("Error adding or updating user for trainee with id " + traineeId, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            String hql = "DELETE FROM Trainee t WHERE t.id = :id";
            int deletedCount = entityManager.createQuery(hql)
                    .setParameter("id", id)
                    .executeUpdate();
            if (deletedCount > 0) {
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting trainee with id: {}", id, e);
            throw new DaoException("Error deleting trainee with id " + id, e);
        }
        log.error("Error deleting trainee with id: {}", id);
        return false;
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
                    return false;
                }
            } else {
                log.error("User with username {} not found", username);
                return false;
            }
        } catch (DaoException e) {
            log.error("Error deleting trainee by username: {}", username, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting trainee by username: {}", username, e);
            throw new DaoException("Unexpected error deleting trainee by username " + username, e);
        }
    }

    @Override
    public boolean deleteTraineeUser(Long traineeId) {
        try {
            Trainee trainee = entityManager.find(Trainee.class, traineeId);

            if (trainee != null) {
                trainee.setUser(null);
                entityManager.merge(trainee);
                entityManager.flush();
                return true;
            } else {
                log.error("Trainee with id {} not found.", traineeId);
                throw new DaoException("Error deleting user for trainee with id " + traineeId + " not found.");
            }
        } catch (Exception e) {
            log.error("Error deleting user for trainee with id: {}", traineeId, e);
            throw new DaoException("Error deleting user for trainee with id " + traineeId, e);
        }
    }

    @Override
    public boolean deleteTrainerFromList(Long traineeId, Long trainerId) {
        try {
            Trainee trainee = entityManager.find(Trainee.class, traineeId);
            if (trainee != null) {
                Trainer trainer = entityManager.find(Trainer.class, trainerId);
                if (trainer != null) {
                    if (trainee.getTrainers().remove(trainer)) {
                        entityManager.merge(trainee);
                        return true;
                    } else {
                        log.error("Trainer with id {} is not associated with trainee with id {}.", trainerId, traineeId);
                        return false;
                    }
                } else {
                    log.error("Trainer with id {} not found.", trainerId);
                    return false;
                }
            } else {
                log.error("Trainee with id {} not found.", traineeId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error removing trainer with id {} from trainee with id: {}", trainerId, traineeId, e);
            throw new DaoException("Error removing trainer with id " + trainerId + " from trainee with id " + traineeId, e);
        }
    }

    @Override
    public boolean deleteTrainingFromList(Long traineeId, Long trainingId) {
        try {
            Trainee trainee = entityManager.find(Trainee.class, traineeId);
            if (trainee != null) {
                Training training = entityManager.find(Training.class, trainingId);
                if (training != null) {
                    if (trainee.getTrainings().remove(training)) {
                        training.setTrainee(null);
                        entityManager.merge(trainee);
                        return true;
                    } else {
                        log.error("Training with id {} is not associated with trainee with id {}.", trainingId, traineeId);
                        return false;
                    }
                } else {
                    log.error("Training with id {} not found.", trainingId);
                    return false;
                }
            } else {
                log.error("Trainee with id {} not found.", traineeId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error removing training with id {} from trainee with id: {}", trainingId, traineeId, e);
            throw new DaoException("Error removing training with id " + trainingId + " from trainee with id " + traineeId, e);
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
        } catch (Exception e) {
            log.error("Error finding trainee by username: {}", username, e);
            throw new DaoException("Error finding trainee by username " + username, e);
        }
    }

    @Override
    public Optional<List<Trainer>> findTrainersNotAssignedToTraineeByUsername(String traineeUsername) {
        try {
            return trainerDao.findTrainersNotAssignedToTraineeByUsername(traineeUsername);
        } catch (DaoException e) {
            log.error("Error retrieving trainers not assigned to trainee with username: {}", traineeUsername, e);
            throw new DaoException("Error retrieving trainers not assigned to trainee with username: " + traineeUsername, e);
        }
    }

    @Override
    public Optional<List<Training>> findTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        try {
            return trainingDao.findTrainingsByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
        } catch (DaoException e) {
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
