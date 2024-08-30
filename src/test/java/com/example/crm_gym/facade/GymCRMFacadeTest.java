package com.example.crm_gym.facade;

import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.TrainingType;
import com.example.crm_gym.services.TraineeService;
import com.example.crm_gym.services.TrainerService;
import com.example.crm_gym.services.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GymCRMFacadeTest {
    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymCRMFacade gymCRMFacade;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTrainees() throws ParseException {
        List<Trainee> mockTrainees = Arrays.asList(new Trainee(5, "Huye", "Benton", "Huye123@gmail.com", "pas11", true, formatter.parse("28-08-2024"), "York 26"));
        when(traineeService.getAllTrainees()).thenReturn(mockTrainees);

        List<Trainee> result = gymCRMFacade.getAllTrainees();

        assertEquals(1, result.size());
        assertEquals("Huye", result.get(0).getFirstName());
        assertEquals("Benton", result.get(0).getLastName());
        assertEquals("Huye123@gmail.com", result.get(0).getUsername());
        assertEquals("pas11", result.get(0).getPassword());
        assertTrue(result.get(0).isActive());
        assertEquals(formatter.parse("28-08-2024"), result.get(0).getDateOfBirth());
        assertEquals("York 26", result.get(0).getAddress());
        verify(traineeService, times(1)).getAllTrainees();
    }

    @Test
    void testGetTraineeById() throws ParseException {
        Trainee trainee = new Trainee(5, "Huye", "Benton", "Huye123@gmail.com", "pas11", true, formatter.parse("28-08-2024"), "York 26");
        when(traineeService.getTrainee(5)).thenReturn(trainee);

        Trainee result = gymCRMFacade.getTraineeById(5);

        assertNotNull(result);
        assertEquals("Huye", result.getFirstName());
        assertEquals("Benton", result.getLastName());
        assertEquals("Huye123@gmail.com", result.getUsername());
        assertEquals("pas11", result.getPassword());
        assertTrue(result.isActive());
        assertEquals(formatter.parse("28-08-2024"), result.getDateOfBirth());
        assertEquals("York 26", result.getAddress());
        verify(traineeService, times(1)).getTrainee(5);
    }

    @Test
    void testCreateTrainee() throws ParseException {
        Trainee trainee = new Trainee(5, "Huye", "Benton", "Huye123@gmail.com", "pas11", true, formatter.parse("28-08-2024"), "York 26");
        doNothing().when(traineeService).createTrainee(trainee.getUserId(), trainee.getFirstName(), trainee.getLastName(), trainee.getDateOfBirth(), trainee.getAddress());

        gymCRMFacade.createTrainee(trainee);

        verify(traineeService, times(1)).createTrainee(trainee.getUserId(), trainee.getFirstName(), trainee.getLastName(), trainee.getDateOfBirth(), trainee.getAddress());
    }

    @Test
    void testUpdateTrainee() throws ParseException {
        Trainee trainee = new Trainee(3, "Alice", "Johnson", "Alice.J123", "password3", true, formatter.parse("03-09-1998"), "Chicago, Oak St 45");
        doNothing().when(traineeService).updateTrainee(3, trainee);

        gymCRMFacade.updateTrainee(3, trainee);

        verify(traineeService, times(1)).updateTrainee(3, trainee);
    }

    @Test
    void testDeleteTrainee() {
        doNothing().when(traineeService).deleteTrainee(1);

        gymCRMFacade.deleteTrainee(1);

        verify(traineeService, times(1)).deleteTrainee(1);
    }

    @Test
    void testGetAllTrainers() {
        List<Trainer> mockTrainers = Arrays.asList(new Trainer(11, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2"));
        when(trainerService.getAllTrainers()).thenReturn(mockTrainers);

        List<Trainer> result = gymCRMFacade.getAllTrainers();

        assertEquals(1, result.size());
        assertEquals("Emma", result.get(0).getFirstName());
        assertEquals("Stown", result.get(0).getLastName());
        assertEquals("emmastown@gmail.com", result.get(0).getUsername());
        assertEquals("pa11s", result.get(0).getPassword());
        assertTrue(result.get(0).isActive());
        assertEquals("spec2", result.get(0).getSpecialization());
        verify(trainerService, times(1)).getAllTrainers();
    }

    @Test
    void testGetTrainerById() {
        Trainer trainer = new Trainer(11, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        when(trainerService.getTrainer(11)).thenReturn(trainer);

        Trainer result = gymCRMFacade.getTrainerById(11);

        assertNotNull(result);
        assertEquals("Emma", result.getFirstName());
        assertEquals("Stown", result.getLastName());
        assertEquals("emmastown@gmail.com", result.getUsername());
        assertEquals("pa11s", result.getPassword());
        assertTrue(result.isActive());
        assertEquals("spec2", result.getSpecialization());
        verify(trainerService, times(1)).getTrainer(11);
    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = new Trainer(11, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        doNothing().when(trainerService).createTrainer(trainer.getUserId(), trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization());
        gymCRMFacade.createTrainer(trainer);
        verify(trainerService, times(1)).createTrainer(trainer.getUserId(), trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization());
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer(6, "Michael", "Scott", "Michael.Scott58", "trainer2", true, "Weightlifting");
        doNothing().when(trainerService).updateTrainer(6, trainer);

        gymCRMFacade.updateTrainer(6, trainer);

        verify(trainerService, times(1)).updateTrainer(6, trainer);
    }

    @Test
    void testDeleteTrainer() {
        doNothing().when(trainerService).deleteTrainer(1);

        gymCRMFacade.deleteTrainer(1);

        verify(trainerService, times(1)).deleteTrainer(1);
    }

    @Test
    void testGetAllTrainings() throws ParseException {
        List<Training> mockTrainings = Arrays.asList(new Training(6, 2,4,"name1", TrainingType.STRENGTH_TRAINING, formatter.parse("29-08-2024"), "55"));
        when(trainingService.getAllTrainings()).thenReturn(mockTrainings);

        List<Training> result = gymCRMFacade.getAllTrainings();

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getTraineeId());
        assertEquals(4, result.get(0).getTrainerId());
        assertEquals("name1", result.get(0).getTrainingName());
        assertEquals(TrainingType.STRENGTH_TRAINING, result.get(0).getTrainingType());
        assertEquals("55", result.get(0).getTrainingDuration());
        assertEquals(formatter.parse("29-08-2024"), result.get(0).getTrainingDate());
        verify(trainingService, times(1)).getAllTrainings();
    }

    @Test
    void testGetTrainingById() throws ParseException {
        Training mockTraining = new Training(1, 2,4,"name1", TrainingType.STRENGTH_TRAINING, formatter.parse("29-08-2024"), "55");
        when(trainingService.getTraining(1)).thenReturn(mockTraining);

        Training result = gymCRMFacade.getTrainingById(1);

        assertEquals(2, result.getTraineeId());
        assertEquals(4, result.getTrainerId());
        assertEquals("name1", result.getTrainingName());
        assertEquals(TrainingType.STRENGTH_TRAINING, result.getTrainingType());
        assertEquals("55", result.getTrainingDuration());
        assertEquals(formatter.parse("29-08-2024"), result.getTrainingDate());
        verify(trainingService, times(1)).getTraining(1);
    }

    @Test
    void testCreateTraining() throws ParseException {
        Training training = new Training(6, 2,4,"name1", TrainingType.STRENGTH_TRAINING, formatter.parse("29-08-2024"), "55");
        doNothing().when(trainingService).createTraining(training);

        gymCRMFacade.createTraining(training);

        verify(trainingService, times(1)).createTraining(training);
    }

    @Test
    void testUpdateTraining() throws ParseException {
        Training training = new Training(3, 3,7,"yoga 11", TrainingType.YOGA, formatter.parse("08-09-2024"), "45");
        doNothing().when(trainingService).updateTraining(3, training);

        gymCRMFacade.updateTraining(3, training);

        verify(trainingService, times(1)).updateTraining(3, training);
    }

    @Test
    void testDeleteTraining() {
        doNothing().when(trainingService).deleteTraining(1);

        gymCRMFacade.deleteTraining(1);

        verify(trainingService, times(1)).deleteTraining(1);
    }
}
