package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingTypeDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.models.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Transactional
@Service
public class TrainingTypeService {

    private TrainingTypeDAO trainingTypeDAO;
    private UserDAO userDAO;

    @Autowired
    public TrainingTypeService(TrainingTypeDAO trainingTypeDAO, UserDAO userDAO) {
        this.trainingTypeDAO = trainingTypeDAO;
        this.userDAO = userDAO;
    }

    public Optional<TrainingType> create(TrainingTypeName name) {
        try {
            TrainingType trainingType = new TrainingType(name);
            Optional<TrainingType> savedTrainingType = trainingTypeDAO.save(trainingType);
            return savedTrainingType;
        } catch (Exception e) {
            log.error("Error creating training type", e);
            throw new ServiceException("Error creating training type", e);
        }
    }

    public Optional<TrainingType> update(TrainingType newTrainingType) {
        try {
            trainingTypeDAO.findById(newTrainingType.getId())
                    .orElseThrow(() -> new ServiceException("Training type not found"));
            return trainingTypeDAO.update(newTrainingType);
        } catch (Exception e) {
            log.error("Error updating training type with id {}: {}", newTrainingType.getId(), e);
            throw new ServiceException("Error updating training type with id " + newTrainingType.getId());
        }
    }

    public boolean delete(Long id) {
        try {
            TrainingType trainingType = trainingTypeDAO.findById(id)
                    .orElseThrow(() -> new ServiceException("Training Type not found"));
            return trainingTypeDAO.delete(trainingType);
        } catch (Exception e) {
            log.error("Error deleting training type with id {}", id, e);
            throw new ServiceException("Error deleting training type with id " + id);
        }
    }

    public Optional<TrainingType> getTrainingTypeById(Long id) {
        try {
            return trainingTypeDAO.findById(id);
        } catch (Exception e) {
            log.error("Error fetching training type with id {}", id, e);
            throw new ServiceException("Error fetching training type with id " + id);
        }
    }

    public List<TrainingType> getAllTrainingTypes() {
        try {
            Optional<List<TrainingType>> trainingTypes = trainingTypeDAO.findAll();
            if (!trainingTypes.isPresent()) {
                log.warn("No training types found.");
                throw new ServiceException("No training types found.");
            }
            return trainingTypes.get();
        } catch (Exception e) {
            log.error("Error fetching all training types", e);
            throw new ServiceException("Error fetching all training types", e);
        }
    }

}
