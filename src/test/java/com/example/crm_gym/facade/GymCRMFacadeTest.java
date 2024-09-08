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
import java.util.Optional;

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
        when(traineeService.getTrainee(5)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = gymCRMFacade.getTraineeById(5);
        assertTrue(result.isPresent());
        assertEquals("Huye", result.get().getFirstName());
        assertEquals("Benton", result.get().getLastName());
        assertEquals("Huye123@gmail.com", result.get().getUsername());
        assertEquals("pas11", result.get().getPassword());
        assertTrue(result.get().isActive());
        assertEquals(formatter.parse("28-08-2024"), result.get().getDateOfBirth());
        assertEquals("York 26", result.get().getAddress());
        verify(traineeService, times(1)).getTrainee(5);
    }

    @Test
    void testCreateTrainee() throws ParseException {
        Trainee trainee = new Trainee(5, "Huye", "Benton", "Huye123@gmail.com", "pas11", true, formatter.parse("28-08-2024"), "York 26");
        when(traineeService.createTrainee(trainee.getUserId(), trainee.getFirstName(), trainee.getLastName(), trainee.getDateOfBirth(), trainee.getAddress()))
                .thenReturn(true);

        boolean result = gymCRMFacade.createTrainee(trainee);

        verify(traineeService, times(1)).createTrainee(trainee.getUserId(), trainee.getFirstName(), trainee.getLastName(), trainee.getDateOfBirth(), trainee.getAddress());
        assertTrue(result);
    }

    @Test
    void testUpdateTrainee() throws ParseException {
        Trainee trainee = new Trainee(3, "Alice", "Johnson", "Alice.J123", "password3", true, formatter.parse("03-09-1998"), "Chicago, Oak St 45");
        when(traineeService.updateTrainee(3, trainee)).thenReturn(true);

        boolean result = gymCRMFacade.updateTrainee(3, trainee);

        verify(traineeService, times(1)).updateTrainee(3, trainee);
        assertTrue(result);
    }

    @Test
    void testDeleteTrainee() {
        when(traineeService.deleteTrainee(1)).thenReturn(true);

        boolean result = gymCRMFacade.deleteTrainee(1);

        verify(traineeService, times(1)).deleteTrainee(1);
        assertTrue(result);
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
        when(trainerService.getTrainer(11)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = gymCRMFacade.getTrainerById(11);

        assertTrue(result.isPresent());
        assertEquals("Emma", result.get().getFirstName());
        assertEquals("Stown", result.get().getLastName());
        assertEquals("emmastown@gmail.com", result.get().getUsername());
        assertEquals("pa11s", result.get().getPassword());
        assertTrue(result.get().isActive());
        assertEquals("spec2", result.get().getSpecialization());
        verify(trainerService, times(1)).getTrainer(11);
    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = new Trainer(11, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        when(trainerService.createTrainer(trainer.getUserId(), trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization())).thenReturn(true);
        boolean result = gymCRMFacade.createTrainer(trainer);
        verify(trainerService, times(1)).createTrainer(trainer.getUserId(), trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization());
        assertTrue(result);
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer(6, "Michael", "Scott", "Michael.Scott58", "trainer2", true, "Weightlifting");
        when(trainerService.updateTrainer(6, trainer)).thenReturn(true);

        boolean result = gymCRMFacade.updateTrainer(6, trainer);

        verify(trainerService, times(1)).updateTrainer(6, trainer);
        assertTrue(result);
    }

    @Test
    void testDeleteTrainer() {
        when(trainerService.deleteTrainer(1)).thenReturn(true);

        boolean result = gymCRMFacade.deleteTrainer(1);

        verify(trainerService, times(1)).deleteTrainer(1);
        assertTrue(result);
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
        when(trainingService.getTraining(1)).thenReturn(Optional.of(mockTraining));

        Optional<Training> result = gymCRMFacade.getTrainingById(1);

        assertEquals(2, result.get().getTraineeId());
        assertEquals(4, result.get().getTrainerId());
        assertEquals("name1", result.get().getTrainingName());
        assertEquals(TrainingType.STRENGTH_TRAINING, result.get().getTrainingType());
        assertEquals("55", result.get().getTrainingDuration());
        assertEquals(formatter.parse("29-08-2024"), result.get().getTrainingDate());
        verify(trainingService, times(1)).getTraining(1);
    }

    @Test
    void testCreateTraining() throws ParseException {
        Training training = new Training(6, 2,4,"name1", TrainingType.STRENGTH_TRAINING, formatter.parse("29-08-2024"), "55");
        when(trainingService.createTraining(training)).thenReturn(true);

        boolean result = gymCRMFacade.createTraining(training);

        verify(trainingService, times(1)).createTraining(training);
        assertTrue(result);
    }

    @Test
    void testUpdateTraining() throws ParseException {
        Training training = new Training(3, 3,7,"yoga 11", TrainingType.YOGA, formatter.parse("08-09-2024"), "45");
        when(trainingService.updateTraining(3, training)).thenReturn(true);

        boolean result = gymCRMFacade.updateTraining(3, training);

        verify(trainingService, times(1)).updateTraining(3, training);
        assertTrue(result);
    }

    @Test
    void testDeleteTraining() {
        when(trainingService.deleteTraining(1)).thenReturn(true);

        boolean result = gymCRMFacade.deleteTraining(1);

        verify(trainingService, times(1)).deleteTraining(1);
        assertTrue(result);
    }
}
