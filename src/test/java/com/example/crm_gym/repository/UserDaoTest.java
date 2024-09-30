package com.example.crm_gym.repository;

import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserDaoTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserDaoImpl userDao;

    @Mock
    private TypedQuery<User> mockQuery;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("John", "Doe");
        user.setUserId(1L);
    }

    @Test
    void testSaveUser() {
        when(userDao.findByFirstAndLastName("John", "Doe")).thenReturn(Optional.empty());
        doNothing().when(entityManager).persist(any(User.class));

        boolean result = userDao.save(user);
        assertTrue(result);
        verify(entityManager, times(1)).persist(user);

        User existingTrainerUser = new User("John", "Doe");
        existingTrainerUser.setTrainer(new Trainer());
        when(userDao.findByFirstAndLastName("John", "Doe")).thenReturn(Optional.of(existingTrainerUser));

        DaoException trainerException = assertThrows(DaoException.class, () -> userDao.save(user));
        assertEquals("User already registered as a Trainer. Cannot register as a Trainee.", trainerException.getMessage());
        verify(entityManager, never()).persist(any(User.class));

        User existingTraineeUser = new User("John", "Doe");
        existingTraineeUser.setTrainee(new Trainee());
        when(userDao.findByFirstAndLastName("John", "Doe")).thenReturn(Optional.of(existingTraineeUser));

        DaoException traineeException = assertThrows(DaoException.class, () -> userDao.save(user));
        assertEquals("User already registered as a Trainee. Cannot register as a Trainer.", traineeException.getMessage());
        verify(entityManager, never()).persist(any(User.class));

        when(userDao.findByFirstAndLastName("John", "Doe")).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Persist failed")).when(entityManager).persist(any(User.class));

        DaoException persistException = assertThrows(DaoException.class, () -> userDao.save(user));
        assertEquals("Error saving user: " + user, persistException.getMessage());
        verify(entityManager, times(2)).persist(user);
    }

    @Test
    void testGenerateUniqueUsername() {
        when(userDao.findAll()).thenReturn(Optional.of(Collections.emptyList()));
        String generatedUsername1 = userDao.generateUniqueUsername("John", "Doe");
        assertEquals("John.Doe", generatedUsername1);

        User user1 = new User("John", "John", true);
        User user2 = new User("John", "John", true);
        List<User> existingUsers = Arrays.asList(user1, user2);
        when(userDao.findAll()).thenReturn(Optional.of(existingUsers));
        String generatedUsername2 = userDao.generateUniqueUsername("John", "Doe");
        assertEquals("John.Doe2", generatedUsername2);

        when(userDao.findAll()).thenReturn(Optional.empty());
        String generatedUsername3 = userDao.generateUniqueUsername("John", "Doe");
        assertEquals("John.Doe", generatedUsername3);
    }

    @Test
    void testUpdateUser() {
        doNothing().when(entityManager).flush();
        when(entityManager.merge(any(User.class))).thenReturn(user);

        Optional<User> updatedUser = userDao.update(user);
        assertTrue(updatedUser.isPresent());
        assertEquals("John", updatedUser.get().getFirstName());
        assertEquals("Doe", updatedUser.get().getLastName());

        verify(entityManager, times(1)).merge(user);
        verify(entityManager, times(1)).flush();

        doThrow(new RuntimeException("Database Error")).when(entityManager).merge(any(User.class));

        DaoException exception = assertThrows(DaoException.class, () -> {
            userDao.update(user);
        });

        assertEquals("Error updating user with id 1", exception.getMessage());
        verify(entityManager, times(2)).merge(user);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(entityManager).remove(user);

        boolean result = userDao.delete(user);
        assertTrue(result);
        verify(entityManager, times(1)).remove(user);

        doThrow(new RuntimeException("Database Error")).when(entityManager).remove(any(User.class));

        DaoException exception = assertThrows(DaoException.class, () -> {
            userDao.delete(user);
        });

        assertEquals("Error deleting user with id 1", exception.getMessage());
        verify(entityManager, times(2)).remove(user);
    }

    @Test
    void testDeleteByUsername() {
        user.setUsername("John.Doe");
        user.setUserId(1L);

        when(userDao.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(entityManager.contains(user)).thenReturn(true);

        boolean resultSuccess = userDao.deleteByUsername("John.Doe");
        assertTrue(resultSuccess);
        verify(entityManager, times(1)).remove(user);

        when(userDao.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(entityManager.contains(user)).thenReturn(false);
        when(entityManager.merge(user)).thenReturn(user);

        boolean resultWithMerge = userDao.deleteByUsername("John.Doe");
        assertTrue(resultWithMerge);
        verify(entityManager, times(1)).merge(user);
        verify(entityManager, times(2)).remove(user);

        when(userDao.findByUsername("John.Doe")).thenReturn(Optional.empty());

        boolean resultNotFound = userDao.deleteByUsername("John.Doe");
        assertFalse(resultNotFound);
        verify(entityManager, times(2)).remove(user);

        when(userDao.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(entityManager.contains(user)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(entityManager).remove(user);

        DaoException exception = assertThrows(DaoException.class, () -> {
            userDao.deleteByUsername("John.Doe");
        });
        assertEquals("Error deleting user by username John.Doe", exception.getMessage());
    }

    @Test
    void testFindById() {
        User user = new User("John", "Doe");
        user.setUserId(1L);

        when(entityManager.find(User.class, 1L)).thenReturn(user);

        Optional<User> foundUser = userDao.findById(1L);
        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
        verify(entityManager, times(1)).find(User.class, 1L);

        when(entityManager.find(User.class, 2L)).thenReturn(null);

        Optional<User> notFoundUser = userDao.findById(2L);
        assertFalse(notFoundUser.isPresent());
        verify(entityManager, times(1)).find(User.class, 2L);

        when(entityManager.find(User.class, 1L)).thenThrow(new RuntimeException("Database error"));

        DaoException exception = assertThrows(DaoException.class, () -> {
            userDao.findById(1L);
        });
        assertEquals("Error finding user with id 1", exception.getMessage());
    }

    @Test
    void testFindByUsername() {
        user.setUsername("John.Doe");

        String hql = "FROM User u WHERE u.username = :username";
        when(entityManager.createQuery(hql, User.class)).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", "John.Doe")).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(user);

        Optional<User> foundUser = userDao.findByUsername("John.Doe");
        assertTrue(foundUser.isPresent());
        assertEquals("John.Doe", foundUser.get().getUsername());
        verify(mockQuery, times(1)).getSingleResult();

        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        DaoException noResultException = assertThrows(DaoException.class, () -> {
            userDao.findByUsername("unknown");
        });
        assertEquals("No user found with username unknown", noResultException.getMessage());
        verify(mockQuery, times(1)).setParameter("username", "unknown");

        when(mockQuery.getSingleResult()).thenThrow(new RuntimeException("Database error"));

        DaoException dbException = assertThrows(DaoException.class, () -> {
            userDao.findByUsername("John.Doe");
        });
        assertEquals("Error finding user with username John.Doe", dbException.getMessage());
    }

    @Test
    void testFindByFirstAndLastName() {
        String hql = "FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName";

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        when(entityManager.createQuery(hql, User.class)).thenReturn(mockQuery);
        when(mockQuery.setParameter("firstName", "John")).thenReturn(mockQuery);
        when(mockQuery.setParameter("lastName", "Doe")).thenReturn(mockQuery);

        when(mockQuery.getSingleResult()).thenReturn(user);

        Optional<User> result = userDao.findByFirstAndLastName("John", "Doe");

        assertTrue(result.isPresent(), "User should be present");
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());

        verify(entityManager, times(1)).createQuery(hql, User.class);
        verify(mockQuery, times(1)).setParameter("firstName", "John");
        verify(mockQuery, times(1)).setParameter("lastName", "Doe");

        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        try {
            userDao.findByFirstAndLastName("John", "Doe");
            fail("DaoException should have been thrown");
        } catch (DaoException e) {
            assertEquals("No user found with firstname John and lastname Doe", e.getMessage());
        }

        when(mockQuery.getSingleResult()).thenThrow(new RuntimeException("Unexpected error"));

        try {
            userDao.findByFirstAndLastName("John", "Doe");
            fail("DaoException should have been thrown");
        } catch (DaoException e) {
            assertEquals("Error finding user with firstname John and lastname Doe", e.getMessage());
        }
    }
    @Test
    void testFindAllUsers() {
        List<User> users = new ArrayList<>();
        User user1 = new User("John", "Doe");
        User user2 = new User("Jane", "Doe");
        users.add(user1);
        users.add(user2);

        when(entityManager.createQuery("FROM User", User.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(users);

        Optional<List<User>> result = userDao.findAll();

        assertTrue(result.isPresent(), "Users should be present");
        assertEquals(2, result.get().size());
        assertEquals("John", result.get().get(0).getFirstName());
        assertEquals("Jane", result.get().get(1).getFirstName());

        verify(entityManager, times(1)).createQuery("FROM User", User.class);
        verify(mockQuery, times(1)).getResultList();

        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        result = userDao.findAll();

        assertTrue(result.isPresent(), "Empty list should be returned, but Optional should be present");
        assertTrue(result.get().isEmpty(), "No users should be found");

        when(mockQuery.getResultList()).thenThrow(new RuntimeException("Unexpected error"));

        try {
            userDao.findAll();
            fail("DaoException should have been thrown");
        } catch (DaoException e) {
            assertEquals("Error finding users", e.getMessage());
        }
    }
}
