package com.example.crm_gym.dao;

import com.example.crm_gym.config.AppConfig;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class TrainerDaoTest {

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

        Optional<Trainer> found = trainerDAO.findById(15);

        assertTrue(found.isPresent());
        assertEquals("Emma", found.get().getFirstName());
        assertEquals("Stown", found.get().getLastName());
        assertEquals("emmastown@gmail.com", found.get().getUsername());
        assertEquals("pas11s", found.get().getPassword());
        assertTrue(found.get().isActive());
        assertEquals("spec2", found.get().getSpecialization());
    }

    @Test
    void testUpdateTrainer() throws ParseException {
        Trainer trainer = new Trainer(15, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        trainerDAO.save(trainer);

        Trainer updatedTrainer = new Trainer(15, "Emma", "Stown", "emmastown@gmail.com", "newpass", false, "Fitness");
        trainerDAO.update(15, updatedTrainer);

        Optional<Trainer> found = trainerDAO.findById(15);
        assertTrue(found.isPresent());
        assertEquals("Emma", found.get().getFirstName());
        assertEquals("Stown", found.get().getLastName());
        assertEquals("emmastown@gmail.com", found.get().getUsername());
        assertEquals("newpass", found.get().getPassword());
        assertFalse(found.get().isActive());
        assertEquals("Fitness", found.get().getSpecialization());
    }

    @Test
    void testDeleteTrainer() throws ParseException {
        int id = 15;
        Trainer trainer = new Trainer(id, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        trainerDAO.save(trainer);

        Optional<Trainer> foundBeforeDelete = trainerDAO.findById(id);
        assertTrue(foundBeforeDelete.isPresent());

        trainerDAO.delete(id);

        DaoException exception = assertThrows(DaoException.class, () -> {
            trainerDAO.findById(id);
        });

        assertEquals("Error finding trainer with id: " + id, exception.getMessage());
    }

    @Test
    void testFindAllTrainers() {
        Optional<List<Trainer>> optionalTrainers = trainerDAO.findAll();
        assertTrue(optionalTrainers.isPresent());
        List<Trainer> trainers = optionalTrainers.get();
        assertNotNull(trainers);
        assertTrue(trainers.size() > 0);
    }
}
