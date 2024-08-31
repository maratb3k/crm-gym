package com.example.crm_gym.dao;

import com.example.crm_gym.config.AppConfig;
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

        Trainee found = traineeDAO.findById(15);

        assertNotNull(found);
        assertEquals("Huye", found.getFirstName());
        assertEquals("Benton", found.getLastName());
        assertEquals("Huye123@gmail.com", found.getUsername());
        assertEquals("pas11", found.getPassword());
        assertTrue(found.isActive());
        assertEquals("York 26", found.getAddress());

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

        Trainee found = traineeDAO.findById(15);
        assertNotNull(found);
        assertEquals("Huye", found.getFirstName());
        assertEquals("Benton", found.getLastName());
        assertEquals("Huye123@gmail.com", found.getUsername());
        assertEquals("newpass", found.getPassword());
        assertFalse(found.isActive());
        assertEquals("York 26", found.getAddress());
        Date parsedDate = formatter.parse("28-08-2024");
        Date expectedDate = trainee.getDateOfBirth();
        assertEquals(parsedDate, expectedDate);
    }

    @Test
    void testDeleteTrainee() throws ParseException {
        Trainee trainee = new Trainee(15, "Huye", "Benton", "Huye123@gmail.com", "pas11", true, formatter.parse("28-08-2024"), "York 26");
        traineeDAO.save(trainee);

        traineeDAO.delete(15);

        Trainee found = traineeDAO.findById(15);
        assertNull(found);
    }

    @Test
    void testFindAllTrainees() {
        List<Trainee> trainees = traineeDAO.findAll();
        assertNotNull(trainees);
        assertTrue(trainees.size() > 0);
    }

}
