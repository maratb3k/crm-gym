package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class TraineeDAOImpl implements TraineeDAO {
    private static final Logger logger = LoggerFactory.getLogger(TraineeDAOImpl.class);

    private Storage storage;

    @Autowired
    public TraineeDAOImpl(Storage storage) {
        this.storage = storage;
    }

    @Override
    public boolean save(Trainee trainee) {
        try {
            storage.save(storage.getTrainees(), trainee.getUserId(), trainee);
            return true;
        } catch (Exception e) {
            logger.error("Error saving trainee: {}", trainee, e);
            return false;
        }
    }

    @Override
    public boolean update(int userId, Trainee trainee) {
        try {
            storage.update(storage.getTrainees(), userId, trainee);
            return true;
        } catch (Exception e) {
            logger.error("Error updating trainee with id: {}", userId, e);
            return false;
        }
    }

    @Override
    public boolean delete(int userId) {
        try {
            storage.remove(storage.getTrainees(), userId);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting trainee with id: {}", userId, e);
            return false;
        }
    }

    @Override
    public Optional<Trainee> findById(int userId) {
        try {
            Trainee trainee = storage.get(storage.getTrainees(), userId);
            if (trainee == null) {
                logger.warn("No trainee found with id: {}", userId);
                throw new DaoException("No trainee found with id: " + userId);
            }
            return Optional.of(trainee);
        } catch (Exception e) {
            logger.error("Error finding trainee with id: {}", userId, e);
            throw new DaoException("Error finding trainee with id: " + userId, e);
        }
    }

    @Override
    public Optional<List<Trainee>> findAll() {
        try {
            List<Trainee> trainees = storage.findAll(storage.getTrainees());
            if (trainees == null || trainees.isEmpty()) {
                logger.warn("No trainees found.");
                throw new DaoException("No trainees found.");
            }
            return Optional.of(trainees);
        } catch (Exception e) {
            logger.error("Error retrieving all trainees", e);
            throw new DaoException("Error retrieving all trainees", e);
        }
    }
}
