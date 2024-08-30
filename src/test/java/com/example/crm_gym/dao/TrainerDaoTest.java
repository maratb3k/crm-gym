package com.example.crm_gym.dao;

import com.example.crm_gym.models.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TrainerDaoTest {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private TrainerDAO trainerDAO;

    @BeforeEach
    void setUp() {
    }

    @Test
    @Sql("/application.properties/initial_data.sql")
    void testSaveTrainer() throws ParseException {
        Trainer trainer = new Trainer(15, "Emma", "Stown", "emmastown@gmail.com", "pas11s", true, "spec2");
        trainerDAO.save(trainer);

        Trainer found = trainerDAO.findById(15);

        assertNotNull(found);
        assertEquals("Emma", found.getFirstName());
        assertEquals("Stown", found.getLastName());
        assertEquals("emmastown@gmail.com", found.getUsername());
        assertEquals("pas11s", found.getPassword());
        assertTrue(found.isActive());
        assertEquals("spec2", found.getSpecialization());
    }

    @Test
    void testUpdateTrainer() throws ParseException {
        Trainer trainer = new Trainer(15, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        trainerDAO.save(trainer);

        Trainer updatedTrainer = new Trainer(15, "Emma", "Stown", "emmastown@gmail.com", "newpass", false, "Fitness");
        trainerDAO.update(15, updatedTrainer);

        Trainer found = trainerDAO.findById(15);
        assertNotNull(found);
        assertEquals("Emma", found.getFirstName());
        assertEquals("Stown", found.getLastName());
        assertEquals("emmastown@gmail.com", found.getUsername());
        assertEquals("newpass", found.getPassword());
        assertFalse(found.isActive());
        assertEquals("Fitness", found.getSpecialization());
    }

    @Test
    void testDeleteTrainer() throws ParseException {
        Trainer trainer = new Trainer(15, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        trainerDAO.save(trainer);

        trainerDAO.delete(15);

        Trainer found = trainerDAO.findById(15);
        assertNull(found);
    }

    @Test
    void testFindAllTrainees() {
        List<Trainer> trainees = trainerDAO.findAll();
        assertNotNull(trainees);
        assertTrue(trainees.size() > 0);
    }
}
