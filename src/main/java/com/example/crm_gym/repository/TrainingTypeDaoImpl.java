package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TrainingTypeDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class TrainingTypeDaoImpl implements TrainingTypeDAO {

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public TrainingTypeDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean save(TrainingType trainingType) {
        try {
            entityManager.persist(trainingType);
            return true;
        } catch (Exception e) {
            log.error("Error saving training type: {}", trainingType, e);
            return false;
        }
    }

    @Override
    public boolean update(Long id, TrainingType updatedTrainingType) {
        try {
            TrainingType existingTrainingType = entityManager.find(TrainingType.class, id);
            if (existingTrainingType != null) {
                if (updatedTrainingType.getName() != null) {
                    existingTrainingType.setName(updatedTrainingType.getName());
                }
                if (updatedTrainingType.getTrainers() != null && !updatedTrainingType.getTrainers().isEmpty()) {
                    existingTrainingType.setTrainers(updatedTrainingType.getTrainers());
                }
                if (updatedTrainingType.getTrainings() != null && !updatedTrainingType.getTrainings().isEmpty()) {
                    existingTrainingType.setTrainings(updatedTrainingType.getTrainings());
                }
                entityManager.merge(existingTrainingType);
                entityManager.flush();
                return true;
            } else {
                log.error("Training type with id {} not found.", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error updating training type with id: {}", id, e);
            throw new DaoException("Error updating training type with id " + id, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            String hql = "DELETE FROM TrainingType t WHERE t.id = :id";
            int deletedCount = entityManager.createQuery(hql)
                    .setParameter("id", id)
                    .executeUpdate();
            if (deletedCount > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new DaoException("Error deleting training type with id " + id, e);
        }
        log.error("Error deleting training type with id: {}", id);
        return false;
    }

    @Override
    public Optional<TrainingType> findById(Long id) {
        try {
            TrainingType trainingType = entityManager.find(TrainingType.class, id);
            return Optional.ofNullable(trainingType);
        } catch (Exception e) {
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
            throw new DaoException("Error finding training types", e);
        }
    }
}
