package com.example.crm_gym.repository;

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

    public TrainingDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Training> save(Training training) {
        try {
            entityManager.persist(training);
            entityManager.flush();
            return Optional.of(training);
        } catch (Exception e) {
            log.error("Error saving training: {}", training, e);
            throw new DaoException("Error saving training " + training, e);
        }
    }

    @Override
    public Optional<Training> update(Training updatedTraining) {
        try {
            entityManager.merge(updatedTraining);
            entityManager.flush();
            return Optional.of(updatedTraining);
        } catch (Exception e) {
            log.error("Error updating training with id: {}", updatedTraining.getId(), e);
            throw new DaoException("Error updating training with id " + updatedTraining.getId(), e);
        }
    }

    @Override
    public boolean delete(Training training) {
        try {
            entityManager.remove(training);
            return true;
        } catch (Exception e) {
            log.error("Error deleting training with id: {}", training.getId());
            throw new DaoException("Error deleting training with id " + training.getId(), e);
        }
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
                throw new DaoException("No trainings found matching the criteria.");
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
                throw new DaoException("No trainings found for trainer username: " + username);
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
