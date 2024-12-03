package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TrainingTypeDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class TrainingTypeDaoImpl implements TrainingTypeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<TrainingType> save(TrainingType trainingType) {
        try {
            entityManager.persist(trainingType);
            return Optional.of(trainingType);
        } catch (Exception e) {
            log.error("Error saving training type: {}", trainingType, e);
            throw new DaoException("Error saving trainee: " + trainingType, e);
        }
    }

    @Override
    public Optional<TrainingType> update(TrainingType updatedTrainingType) {
        try {
            entityManager.merge(updatedTrainingType);
            entityManager.flush();
            return Optional.of(updatedTrainingType);
        } catch (Exception e) {
            log.error("Error updating training type with id: {}", updatedTrainingType.getId(), e);
            throw new DaoException("Error updating training type with id " + updatedTrainingType.getId(), e);
        }
    }

    @Override
    public boolean delete(TrainingType trainingType) {
        try {
            entityManager.remove(trainingType);
            return true;
        } catch (Exception e) {
            log.error("Error deleting training type with id: {}", trainingType.getId(), e);
            throw new DaoException("Error deleting training type with id " + trainingType.getId(), e);
        }
    }

    @Override
    public Optional<TrainingType> findById(Long id) {
        try {
            TrainingType trainingType = entityManager.find(TrainingType.class, id);
            return Optional.ofNullable(trainingType);
        } catch (Exception e) {
            log.error("Error finding training type with id: {}", id, e);
            throw new DaoException("Error finding training type with id " + id, e);
        }
    }

    @Override
    public Optional<List<TrainingType>> findAll() {
        try {
            String hql = "FROM TrainingType";
            List<TrainingType> trainingTypes = entityManager.createQuery(hql, TrainingType.class).getResultList();
            return Optional.ofNullable(trainingTypes);
        } catch (Exception e) {
            log.error("Error finding training type list", e);
            throw new DaoException("Error finding training types", e);
        }
    }
}
