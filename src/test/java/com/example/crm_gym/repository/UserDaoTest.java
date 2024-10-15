package com.example.crm_gym.repository;

import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.User;
import com.example.crm_gym.services.MetricsService;
import com.example.crm_gym.utils.UserProfileUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import io.micrometer.core.instrument.Timer;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {
    @Mock
    EntityManager entityManager;

    @InjectMocks
    private UserDaoImpl userDao;

    @Mock
    private MetricsService metricsService;

    @Mock
    private TypedQuery<User> typedQuery;

    @Mock
    private Timer.Sample timerSample;

    @Mock
    private UserProfileUtil userProfileUtil;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(metricsService.startQueryTimer()).thenReturn(timerSample);
        user = new User("John", "Doe");
        user.setUserId(1L);
    }

    @Test
    void testFindById() {
        User user = new User("John", "Doe");
        when(entityManager.find(User.class, 1L)).thenReturn(user);
        Optional<User> result = userDao.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());

        when(entityManager.find(User.class, 1L)).thenThrow(new RuntimeException("Database error"));
        DaoException exception = assertThrows(DaoException.class, () -> {
            userDao.findById(1L);
        });
        assertTrue(exception.getMessage().contains("Error finding user with id"));
    }

    @Test
    void testFindByUsername() {
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("username"), anyString())).thenReturn(typedQuery);
        User user = new User("John", "Doe");
        when(typedQuery.getSingleResult()).thenReturn(user);
        Optional<User> result = userDao.findByUsername("John.Doe");
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("username"), anyString())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new NoResultException());
        DaoException noResultException = assertThrows(DaoException.class, () -> {
            userDao.findByUsername("Unknown.User");
        });
        assertTrue(noResultException.getMessage().contains("No user found with username"));

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("username"), anyString())).thenReturn(typedQuery);
        lenient().when(typedQuery.getSingleResult()).thenThrow(new RuntimeException("Database error"));
        DaoException generalException = assertThrows(DaoException.class, () -> {
            userDao.findByUsername("Error.User");
        });
        assertTrue(generalException.getMessage().contains("Error finding user with username"));
    }

    @Test
    void testFindAll() {
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(new User("John", "Doe")));
        Optional<List<User>> resultWithUsers = userDao.findAll();
        assertTrue(resultWithUsers.isPresent());
        assertEquals(1, resultWithUsers.get().size());
        assertEquals("John", resultWithUsers.get().get(0).getFirstName());

        when(typedQuery.getResultList()).thenReturn(List.of());
        Optional<List<User>> resultEmpty = userDao.findAll();
        assertTrue(resultEmpty.isPresent());
        assertTrue(resultEmpty.get().isEmpty());
    }

    @Test
    void testFindByFirstAndLastName() {
        User user = new User("John", "Doe");
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("firstName"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("lastName"), anyString())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(user));
        Optional<User> result = userDao.findByFirstAndLastName("John", "Doe");
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());

        when(typedQuery.getResultList()).thenReturn(List.of());
        result = userDao.findByFirstAndLastName("John", "Doe");
        assertFalse(result.isPresent());

        when(typedQuery.getResultList()).thenThrow(new RuntimeException("Database error"));
        DaoException exception = assertThrows(DaoException.class, () -> {
            userDao.findByFirstAndLastName("John", "Doe");
        });
        assertTrue(exception.getMessage().contains("Error finding user with firstname John and lastname Doe"));
    }
}