package com.example.crm_gym.services;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.models.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class TraineeServiceTest {
    @Mock
    private TraineeDAO traineeDAO;

    @InjectMocks
    private TraineeService traineeService;

    SimpleDateFormat formatter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        formatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Test
    void testCreateTrainee() throws ParseException {
        String firstName = "Huye";
        String lastName = "Benton";
        Date dateOfBirth = formatter.parse("28-08-2024");
        String address = "York 26";
        List<Trainee> existingTrainees = new ArrayList<>();

        when(traineeDAO.findAll()).thenReturn(existingTrainees);
        doNothing().when(traineeDAO).save(any(Trainee.class));

        traineeService.createTrainee(5, firstName, lastName, dateOfBirth, address);

        verify(traineeDAO, times(1)).save(any(Trainee.class));
    }

    @Test
    void testGenerateUniqueUsername() {
        String firstName = "John";
        String lastName = "Doe";
        List<Trainee> existingTrainees = new ArrayList<>();
        existingTrainees.add(new Trainee(1, "John", "Doe", "John.Doe", "pass123", true, new Date(), "Some address"));
        when(traineeDAO.findAll()).thenReturn(existingTrainees);

        traineeService.createTrainee(2, firstName, lastName, new Date(), "address");

        assertNotNull(existingTrainees.get(0).getUsername());
        verify(traineeDAO, times(1)).save(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee() throws ParseException {
        Trainee trainee = new Trainee(15, "Huye", "Benton", "Huye123@gmail.com", "pas11", true, formatter.parse("28-08-2024"), "York 26");
        doNothing().when(traineeDAO).update(15, trainee);
        traineeService.updateTrainee(15, trainee);
        verify(traineeDAO, times(1)).update(15, trainee);
    }

    @Test
    void testDeleteTrainee() {
        doNothing().when(traineeDAO).delete(1);
        traineeService.deleteTrainee(1);
        verify(traineeDAO, times(1)).delete(1);
    }

    @Test
    void testGetTrainee() throws ParseException {
        Trainee trainee = new Trainee(10, "Huye", "Benton", "Huye123@gmail.com", "pas11", true, formatter.parse("28-08-2024"), "York 26");
        when(traineeDAO.findById(10)).thenReturn(trainee);
        Trainee result = traineeService.getTrainee(10);
        assertEquals(trainee, result);
        verify(traineeDAO, times(1)).findById(10);
    }

    @Test
    void testGetAllTrainees() throws ParseException {
        List<Trainee> trainees = Arrays.asList(new Trainee(10, "Huye", "Benton", "Huye123@gmail.com", "pas11", true, formatter.parse("28-08-2024"), "York 26"));
        when(traineeDAO.findAll()).thenReturn(trainees);
        List<Trainee> result = traineeService.getAllTrainees();
        assertEquals(trainees, result);
        verify(traineeDAO, times(1)).findAll();
    }
}
