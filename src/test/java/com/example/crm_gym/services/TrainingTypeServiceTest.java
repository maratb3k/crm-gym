package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingTypeDAO;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.models.TrainingType;
import com.example.crm_gym.models.TrainingTypeName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeDAO trainingTypeDAO;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainingType = new TrainingType(TrainingTypeName.STRENGTH);
        trainingType.setId(1L);
    }

    @Test
    void testCreateTrainingType() {
        when(trainingTypeDAO.save(any(TrainingType.class))).thenReturn(Optional.of(trainingType));

        Optional<TrainingType> createdTrainingType = trainingTypeService.create(TrainingTypeName.STRENGTH);

        assertTrue(createdTrainingType.isPresent());
        assertEquals(TrainingTypeName.STRENGTH, createdTrainingType.get().getName());
        verify(trainingTypeDAO, times(1)).save(any(TrainingType.class));
    }

    @Test
    void testCreateTrainingTypeThrowsException() {
        when(trainingTypeDAO.save(any(TrainingType.class))).thenThrow(new ServiceException("Error creating training type"));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            trainingTypeService.create(TrainingTypeName.STRENGTH);
        });

        assertEquals("Error creating training type", exception.getMessage());
        verify(trainingTypeDAO, times(1)).save(any(TrainingType.class));
    }

    @Test
    void testUpdateTrainingType() {
        when(trainingTypeDAO.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainingTypeDAO.update(any(TrainingType.class))).thenReturn(Optional.of(trainingType));

        Optional<TrainingType> updatedTrainingType = trainingTypeService.update(trainingType);

        assertTrue(updatedTrainingType.isPresent());
        verify(trainingTypeDAO, times(1)).findById(1L);
        verify(trainingTypeDAO, times(1)).update(any(TrainingType.class));
    }

    @Test
    void testUpdateTrainingTypeThrowsException() {
        when(trainingTypeDAO.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            trainingTypeService.update(trainingType);
        });

        assertEquals("Training type not found", exception.getMessage());
        verify(trainingTypeDAO, times(1)).findById(1L);
        verify(trainingTypeDAO, times(0)).update(any(TrainingType.class));
    }

    @Test
    void testDeleteTrainingType() {
        when(trainingTypeDAO.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainingTypeDAO.delete(trainingType)).thenReturn(true);

        boolean result = trainingTypeService.delete(1L);

        assertTrue(result);
        verify(trainingTypeDAO, times(1)).findById(1L);
        verify(trainingTypeDAO, times(1)).delete(trainingType);
    }

    @Test
    void testDeleteTrainingTypeThrowsException() {
        when(trainingTypeDAO.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            trainingTypeService.delete(1L);
        });

        assertEquals("Training Type not found", exception.getMessage());
        verify(trainingTypeDAO, times(1)).findById(1L);
        verify(trainingTypeDAO, times(0)).delete(any(TrainingType.class));
    }

    @Test
    void testGetAllTrainingTypes() {
        List<TrainingType> trainingTypeList = Arrays.asList(trainingType);
        when(trainingTypeDAO.findAll()).thenReturn(Optional.of(trainingTypeList));

        List<TrainingType> result = trainingTypeService.getAllTrainingTypes();

        assertEquals(1, result.size());
        verify(trainingTypeDAO, times(1)).findAll();
    }

    @Test
    void testGetAllTrainingTypesThrowsException() {
        when(trainingTypeDAO.findAll()).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            trainingTypeService.getAllTrainingTypes();
        });

        assertEquals("No training types found.", exception.getMessage());
        verify(trainingTypeDAO, times(1)).findAll();
    }
}
