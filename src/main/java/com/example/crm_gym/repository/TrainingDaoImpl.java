package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class TrainingDaoImpl implements TrainingDAO {

    @PersistenceContext
    private final EntityManager entityManager;
    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;

    public TrainingDaoImpl(EntityManager entityManager, TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        this.entityManager = entityManager;
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    @Override
    public boolean save(Training training) {
        try {
            entityManager.persist(training);
            return true;
        } catch (Exception e) {
            log.error("Error saving training: {}", training, e);
            throw new DaoException("Error saving training " + training, e);
        }
    }

    @Override
    public boolean update(Long id, Training updatedTraining) {
        try {
            Training existingTraining = entityManager.find(Training.class, id);
            if (existingTraining != null) {
                if (updatedTraining.getTrainingName() != null) {
                    existingTraining.setTrainingName(updatedTraining.getTrainingName());
                }
                if (updatedTraining.getTrainee() != null) {
                    existingTraining.setTrainee(updatedTraining.getTrainee());
                }
                if (updatedTraining.getTrainer() != null) {
                    existingTraining.setTrainer(updatedTraining.getTrainer());
                }
                if (updatedTraining.getTrainingType() != null) {
                    existingTraining.setTrainingType(updatedTraining.getTrainingType());
                }
                if (updatedTraining.getTrainingDate() != null) {
                    existingTraining.setTrainingDate(updatedTraining.getTrainingDate());
                }
                if (updatedTraining.getTrainingDuration() > 0) {
                    existingTraining.setTrainingDuration(updatedTraining.getTrainingDuration());
                }
                entityManager.merge(existingTraining);
                entityManager.flush();
                return true;
            } else {
                log.error("Training with id {} not found.", id);
                throw new DaoException("Error updating training with id " + id);
            }
        } catch (Exception e) {
            log.error("Error updating training with id: {}", id, e);
            throw new DaoException("Error updating training with id " + id, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            String hql = "DELETE FROM Training t WHERE t.id = :id";
            int deletedCount = entityManager.createQuery(hql)
                    .setParameter("id", id)
                    .executeUpdate();
            if (deletedCount > 0) {
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting training with id: {}", id);
            throw new DaoException("Error deleting training with id " + id, e);
        }
        log.error("Error deleting training with id: {}", id);
        return false;
    }

    @Override
    public Optional<Training> findById(Long id) {
        try {
            Training training = entityManager.find(Training.class, id);
            return Optional.ofNullable(training);
        } catch (Exception e) {
            log.error("Error finding training with id: {}", id, e);
            throw new DaoException("Error finding training with id " + id, e);
        }
    }

    @Override
    public Optional<List<Training>> findAll() {
        try {
            String hql = "FROM Training";
            List<Training> trainings = entityManager.createQuery(hql, Training.class).getResultList();
            return Optional.ofNullable(trainings);
        } catch (Exception e) {
            log.error("Error finding trainings", e);
            throw new DaoException("Error finding trainings", e);
        }
    }

    @Override
    public Optional<List<Training>> findTrainingsByTraineeUsernameAndCriteria(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Training> query = cb.createQuery(Training.class);
            Root<Training> training = query.from(Training.class);

            Join<Training, Trainee> trainee = training.join("trainee");
            Join<Training, Trainer> trainer = training.join("trainer");
            Join<Training, TrainingType> trainingType = training.join("trainingType");

            Predicate criteria = cb.conjunction();

            if (username != null && !username.isEmpty()) {
                criteria = cb.and(criteria, cb.equal(trainee.get("user").get("username"), username));
            }

            if (fromDate != null) {
                criteria = cb.and(criteria, cb.greaterThanOrEqualTo(training.get("trainingDate"), fromDate));
            }
            if (toDate != null) {
                criteria = cb.and(criteria, cb.lessThanOrEqualTo(training.get("trainingDate"), toDate));
            }

            if (trainerName != null && !trainerName.isEmpty()) {
                criteria = cb.and(criteria, cb.equal(trainer.get("user").get("username"), trainerName));
            }

            if (trainingTypeName != null && !trainingTypeName.isEmpty()) {
                criteria = cb.and(criteria, cb.equal(trainingType.get("name"), trainingTypeName));
            }

            query.select(training).where(criteria);

            TypedQuery<Training> typedQuery = entityManager.createQuery(query);
            List<Training> results = typedQuery.getResultList();

            if (results.isEmpty()) {
                log.info("No trainings found matching the criteria.");
                return Optional.empty();
            } else {
                log.info("Found {} trainings matching the criteria.", results.size());
                return Optional.of(results);
            }

        } catch (Exception e) {
            log.error("Error retrieving trainings for trainee username: {}", username, e);
            throw new DaoException("Error retrieving trainings for trainee username: " + username, e);
        }
    }

    @Override
    public Optional<List<Training>> findTrainingsByTrainerUsernameAndCriteria(String username, Date fromDate, Date toDate, String traineeName) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Training> query = cb.createQuery(Training.class);
            Root<Training> training = query.from(Training.class);

            Join<Training, Trainer> trainer = training.join("trainer");
            Join<Training, Trainee> trainee = training.join("trainee");

            Predicate criteria = cb.conjunction();

            if (username != null && !username.isEmpty()) {
                criteria = cb.and(criteria, cb.equal(trainer.get("user").get("username"), username));
            }
            if (fromDate != null) {
                criteria = cb.and(criteria, cb.greaterThanOrEqualTo(training.get("trainingDate"), fromDate));
            }
            if (toDate != null) {
                criteria = cb.and(criteria, cb.lessThanOrEqualTo(training.get("trainingDate"), toDate));
            }
            if (traineeName != null && !traineeName.isEmpty()) {
                criteria = cb.and(criteria, cb.equal(trainee.get("user").get("username"), traineeName));
            }

            query.select(training).where(criteria);
            TypedQuery<Training> typedQuery = entityManager.createQuery(query);
            List<Training> results = typedQuery.getResultList();

            if (results.isEmpty()) {
                log.info("No trainings found for trainer username: {} with the specified criteria.", username);
                return Optional.empty();
            } else {
                log.info("Found {} trainings for trainer username: {} with the specified criteria.", results.size(), username);
                return Optional.of(results);
            }

        } catch (Exception e) {
            log.error("Error retrieving trainings for trainer username: {}", username, e);
            throw new DaoException("Error retrieving trainings for trainer username: " + username, e);
        }
    }

}
