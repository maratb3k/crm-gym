package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TraineeDAOImpl implements TraineeDAO {
    private static final Logger logger = LoggerFactory.getLogger(TraineeDAOImpl.class);

    private Storage storage;
    private Map<Integer, Trainee> traineeStorage;

    @Autowired
    public TraineeDAOImpl(Storage storage) {
        this.storage = storage;
        this.traineeStorage = storage.getTrainees();
        logger.info("TraineeDAOImpl initialized with Storage: {}", storage);
    }

    @Override
    public void save(Trainee trainee) {
        logger.info("Entering save() with trainee: {}", trainee);
        try {
            storage.save(traineeStorage, trainee.getUserId(), trainee);
            logger.info("Trainee saved successfully: {}", trainee);
        } catch (Exception e) {
            logger.error("Error saving trainee: {}", trainee, e);
        } finally {
            logger.info("Exiting save() with trainee: {}", trainee);
        }
    }

    @Override
    public void update(int userId, Trainee trainee) {
        logger.info("Entering update() with userId: {} and trainee: {}", userId, trainee);
        try {
            storage.update(traineeStorage, userId, trainee);
            logger.info("Trainee updated successfully: {}", trainee);
        } catch (Exception e) {
            logger.error("Error updating trainee with userId: {}", userId, e);
        } finally {
            logger.info("Exiting update() with userId: {}", userId);
        }
    }

    @Override
    public void delete(int userId) {
        logger.info("Entering delete() with userId: {}", userId);
        try {
            storage.remove(traineeStorage, userId);
            logger.info("Trainee deleted successfully with userId: {}", userId);
        } catch (Exception e) {
            logger.error("Error deleting trainee with userId: {}", userId, e);
        } finally {
            logger.info("Exiting delete() with userId: {}", userId);
        }
    }

    @Override
    public Trainee findById(int userId) {
        logger.info("Entering findById() with userId: {}", userId);
        try {
            Trainee trainee = storage.get(traineeStorage, userId);
            if (trainee != null) {
                logger.info("Trainee found: {}", trainee);
            } else {
                logger.warn("No trainee found with userId: {}", userId);
            }
            return trainee;
        } catch (Exception e) {
            logger.error("Error finding trainee with userId: {}", userId, e);
            return null;
        } finally {
            logger.info("Exiting findById() with userId: {}", userId);
        }
    }

    @Override
    public List<Trainee> findAll() {
        logger.info("Entering findAll()");
        try {
            List<Trainee> trainees = storage.findAll(traineeStorage);
            logger.info("Returning all trainees, count: {}", trainees.size());
            return trainees;
        } catch (Exception e) {
            logger.error("Error retrieving all trainees", e);
            return null;
        } finally {
            logger.info("Exiting findAll()");
        }
    }
}
