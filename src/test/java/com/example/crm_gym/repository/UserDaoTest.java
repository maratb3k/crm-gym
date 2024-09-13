package com.example.crm_gym.repository;

import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDaoTest {
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserDaoImpl userDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        User user = new User("John", "Doe", "johndoe", "password123", true);

        doNothing().when(entityManager).persist(any(User.class));
        boolean successResult = userDao.save(user);
        assertTrue(successResult);
        verify(entityManager, times(1)).persist(user);

        doThrow(new RuntimeException()).when(entityManager).persist(any(User.class));
        boolean failureResult = userDao.save(user);
        assertFalse(failureResult);
        verify(entityManager, times(2)).persist(user);
    }

    @Test
    void testUpdate() {
        User existingUser = new User("John", "Doe", "johndoe", "password123", true);
        existingUser.setUserId(1L);

        User updatedUser = new User("Jane", "Doe", "janedoe", "newpassword", true);

        when(entityManager.find(User.class, 1L)).thenReturn(existingUser);
        boolean successResult = userDao.update(1L, updatedUser);
        assertTrue(successResult);
        verify(entityManager, times(1)).find(User.class, 1L);
        verify(entityManager, times(1)).merge(any(User.class));

        reset(entityManager);

        when(entityManager.find(User.class, 1L)).thenReturn(null);
        boolean failureResult = userDao.update(1L, updatedUser);
        assertFalse(failureResult);
        verify(entityManager, times(1)).find(User.class, 1L);
        verify(entityManager, times(0)).merge(any(User.class));
    }

    @Test
    void testUpdatePassword() {
        User user = new User("John", "Doe", "johndoe", "password123", true);
        user.setUserId(1L);

        when(entityManager.find(User.class, 1L)).thenReturn(user);
        boolean successResult = userDao.updatePassword(1L, "newpassword");
        assertTrue(successResult);
        verify(entityManager, times(1)).find(User.class, 1L);
        verify(entityManager, times(1)).merge(any(User.class));

        reset(entityManager);

        when(entityManager.find(User.class, 1L)).thenReturn(null);
        boolean failureResult = userDao.updatePassword(1L, "newpassword");
        assertFalse(failureResult);
        verify(entityManager, times(1)).find(User.class, 1L);
    }

    @Test
    void testDelete() {
        String hql = "DELETE FROM User u WHERE u.userId = :id";

        when(entityManager.createQuery(hql)).thenReturn(mock(Query.class));
        when(entityManager.createQuery(hql).setParameter("id", 1L).executeUpdate()).thenReturn(1);
        boolean successResult = userDao.delete(1L);
        assertTrue(successResult);
        verify(entityManager, times(1)).createQuery(hql);

        reset(entityManager);

        when(entityManager.createQuery(hql)).thenReturn(mock(Query.class));
        when(entityManager.createQuery(hql).setParameter("id", 1L).executeUpdate()).thenReturn(0);
        boolean failureResult = userDao.delete(1L);
        assertFalse(failureResult);
        verify(entityManager, times(1)).createQuery(hql);
    }

    @Test
    void testFindById() {
        User user = new User("John", "Doe", "johndoe", "password123", true);
        user.setUserId(1L);

        when(entityManager.find(User.class, 1L)).thenReturn(user);
        Optional<User> successResult = userDao.findById(1L);
        assertTrue(successResult.isPresent());
        assertEquals("johndoe", successResult.get().getUsername());
        verify(entityManager, times(1)).find(User.class, 1L);
    }

    @Test
    void testFindByUsername() {
        User user = new User("John", "Doe", "johndoe", "password123", true);
        String hql = "FROM User u WHERE u.username = :username";

        when(entityManager.createQuery(hql, User.class)).thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery(hql, User.class).setParameter("username", "johndoe").getSingleResult()).thenReturn(user);
        Optional<User> successResult = userDao.findByUsername("johndoe");
        assertTrue(successResult.isPresent());
        assertEquals("johndoe", successResult.get().getUsername());
        verify(entityManager, times(1)).createQuery(hql, User.class);

        reset(entityManager);

        when(entityManager.createQuery(hql, User.class)).thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery(hql, User.class).setParameter("username", "unknown").getSingleResult()).thenThrow(NoResultException.class);
        Optional<User> failureResult = userDao.findByUsername("unknown");
        assertFalse(failureResult.isPresent());
        verify(entityManager, times(1)).createQuery(hql, User.class);
    }



}
