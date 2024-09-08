package com.example.crm_gym.dao;

import com.example.crm_gym.config.AppConfig;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class TraineeDaoTest {

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private TraineeDAO traineeDAO;

    @BeforeEach
    void setUp() {
    }

    @Test
    @Sql("/application.properties/initial_data.sql")
    void testSaveTrainee() throws ParseException {
        Trainee trainee = new Trainee(15, "Huye", "Benton", "Huye123@gmail.com", "pas11", true, formatter.parse("28-08-2024"), "York 26");
        traineeDAO.save(trainee);

        Optional<Trainee> found = traineeDAO.findById(15);

        assertTrue(found.isPresent());
        assertEquals("Huye", found.get().getFirstName());
        assertEquals("Benton", found.get().getLastName());
        assertEquals("Huye123@gmail.com", found.get().getUsername());
        assertEquals("pas11", found.get().getPassword());
        assertTrue(found.get().isActive());
        assertEquals("York 26", found.get().getAddress());

        Date parsedDate = formatter.parse("28-08-2024");
        Date expectedDate = trainee.getDateOfBirth();
        assertEquals(parsedDate, expectedDate);
    }

    @Test
    void testUpdateTrainee() throws ParseException {
        Trainee trainee = new Trainee(15, "Huye", "Benton", "Huye123@gmail.com", "pas11", true, formatter.parse("28-08-2024"), "York 26");
        traineeDAO.save(trainee);

        Trainee updatedTrainee = new Trainee(15, "Huye", "Benton", "Huye123@gmail.com", "newpass", false, formatter.parse("28-08-2024"), "York 26");
        traineeDAO.update(15, updatedTrainee);

        Optional<Trainee> found = traineeDAO.findById(15);
        assertTrue(found.isPresent());
        assertEquals("Huye", found.get().getFirstName());
        assertEquals("Benton", found.get().getLastName());
        assertEquals("Huye123@gmail.com", found.get().getUsername());
        assertEquals("newpass", found.get().getPassword());
        assertFalse(found.get().isActive());
        assertEquals("York 26", found.get().getAddress());
        Date parsedDate = formatter.parse("28-08-2024");
        Date expectedDate = trainee.getDateOfBirth();
        assertEquals(parsedDate, expectedDate);
    }

    @Test
    void testDeleteTrainee() throws ParseException {
        int userId = 15;
        Trainee trainee = new Trainee(userId, "Huye", "Benton", "Huye.123", "pas11", true,
                new SimpleDateFormat("yyyy-MM-dd").parse("2024-08-28"), "York 26");
        traineeDAO.save(trainee);
        Optional<Trainee> foundBeforeDelete = traineeDAO.findById(userId);
        assertTrue(foundBeforeDelete.isPresent());

        traineeDAO.delete(userId);

        DaoException exception = assertThrows(DaoException.class, () -> {
            traineeDAO.findById(userId);
        });

        assertEquals("Error finding trainee with id: " + userId, exception.getMessage());
    }

    @Test
    void testFindAllTrainees() {
        Optional<List<Trainee>> optionalTrainees = traineeDAO.findAll();
        assertTrue(optionalTrainees.isPresent());
        List<Trainee> trainees = optionalTrainees.get();
        assertNotNull(trainees);
        assertFalse(trainees.isEmpty());
    }

}
