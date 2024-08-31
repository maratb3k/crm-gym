package com.example.crm_gym.repository;

import com.example.crm_gym.storage.Storage;
import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.models.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TrainerDAOImpl implements TrainerDAO {
    private static final Logger logger = LoggerFactory.getLogger(TrainerDAOImpl.class);

    private Storage storage;
    private Map<Integer, Trainer> trainerStorage;

    @Autowired
    public TrainerDAOImpl(Storage storage) {
        this.storage = storage;
        this.trainerStorage = storage.getTrainers();
        logger.info("TrainerDAOImpl initialized with Storage: {}", storage);
    }

    @Override
    public void save(Trainer trainer) {
        logger.info("Entering save() with trainer: {}", trainer);
        try {
            storage.save(trainerStorage, trainer.getUserId(), trainer);
            logger.info("Trainer saved successfully: {}", trainer);
        } catch (Exception e) {
            logger.error("Error saving trainer: {}", trainer, e);
        } finally {
            logger.info("Exiting save() with trainer: {}", trainer);
        }
    }

    @Override
    public void update(int userId, Trainer trainer) {
        logger.info("Entering update() with userId: {} and trainer: {}", userId, trainer);
        try {
            storage.update(trainerStorage, userId, trainer);
            logger.info("Trainer updated successfully: {}", trainer);
        } catch (Exception e) {
            logger.error("Error updating trainer with userId: {}", userId, e);
        } finally {
            logger.info("Exiting update() with userId: {}", userId);
        }
    }

    @Override
    public void delete(int userId) {
        logger.info("Entering delete() with userId: {}", userId);
        try {
            storage.remove(trainerStorage, userId);
            logger.info("Trainer deleted successfully with userId: {}", userId);
        } catch (Exception e) {
            logger.error("Error deleting trainer with userId: {}", userId, e);
        } finally {
            logger.info("Exiting delete() with userId: {}", userId);
        }
    }

    @Override
    public Trainer findById(int userId) {
        logger.info("Entering findById() with userId: {}", userId);
        try {
            Trainer trainer = storage.get(trainerStorage, userId);
            if (trainer != null) {
                logger.info("Trainer found: {}", trainer);
            } else {
                logger.warn("No trainer found with userId: {}", userId);
            }
            return trainer;
        } catch (Exception e) {
            logger.error("Error finding trainer with userId: {}", userId, e);
            return null;
        } finally {
            logger.info("Exiting findById() with userId: {}", userId);
        }
    }

    @Override
    public List<Trainer> findAll() {
        logger.info("Entering findAll()");
        try {
            List<Trainer> trainers = storage.findAll(trainerStorage);
            logger.info("Returning all trainers, count: {}", trainers.size());
            return trainers;
        } catch (Exception e) {
            logger.error("Error retrieving all trainers", e);
            return null;
        } finally {
            logger.info("Exiting findAll()");
        }
    }
}
