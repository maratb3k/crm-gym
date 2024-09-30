package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingTypeDAO;
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
public class  TrainingTypeService extends BaseService<TrainingType> {

    private TrainingTypeDAO trainingTypeDAO;

    @Autowired
    public TrainingTypeService(TrainingTypeDAO trainingTypeDAO) {
        super(trainingTypeDAO);
        this.trainingTypeDAO = trainingTypeDAO;
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
            findEntityById(newTrainingType.getId())
                    .orElseThrow(() -> new ServiceException("Training type not found"));
            return trainingTypeDAO.update(newTrainingType);
        } catch (Exception e) {
            log.error("Error updating training type with id {}: {}", newTrainingType.getId(), e);
            throw new ServiceException("Error updating training type with id " + newTrainingType.getId(), e);
        }
    }

    public boolean delete(Long id) {
        try {
            TrainingType trainingType = findEntityById(id)
                    .orElseThrow(() -> new ServiceException("Training Type not found"));
            return trainingTypeDAO.delete(trainingType);
        } catch (Exception e) {
            log.error("Error deleting training type with id {}", id, e);
            throw new ServiceException("Error deleting training type with id " + id, e);
        }
    }

    public Optional<TrainingType> getTrainingTypeById(Long id) {
        try {
            return trainingTypeDAO.findById(id);
        } catch (Exception e) {
            log.error("Error fetching training type with id {}", id, e);
            throw new ServiceException("Error fetching training type with id " + id, e);
        }
    }

    public List<TrainingType> getAllTrainingTypes() {
        try {
            Optional<List<TrainingType>> trainingTypes = trainingTypeDAO.findAll();
            if (trainingTypes.isPresent()) {
                return trainingTypes.get();
            } else {
                log.warn("No training types found.");
                throw new ServiceException("No training types found.");
            }
        } catch (Exception e) {
            log.error("Error fetching all training types", e);
            throw new ServiceException("Error fetching all training types", e);
        }
    }

}
