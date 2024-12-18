package com.example.crm_gym.repository;

import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.User;
import com.example.crm_gym.services.MetricsService;
import com.example.crm_gym.utils.UserProfileUtil;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class UserDaoImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final MetricsService metricsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDaoImpl(MetricsService metricsService, PasswordEncoder passwordEncoder) {
        this.metricsService = metricsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean save(User user) {
        Timer.Sample timerSample = metricsService.startQueryTimer();
        try {
            Optional<User> optUser = findByFirstAndLastName(user.getFirstName(), user.getLastName());
            if (optUser.isPresent()) {
                User existingUser = optUser.get();
                if (existingUser.getTrainer() != null) {
                    throw new DaoException("User already registered as a Trainer.");
                } else if (existingUser.getTrainee() != null) {
                    throw new DaoException("User already registered as a Trainee.");
                }
            }
            String username = generateUniqueUsername(user.getFirstName(), user.getLastName());
            String pass = UserProfileUtil.generatePassword();
            String hashPass = passwordEncoder.encode(pass);
            user.setUsername(username);
            user.setPassword(hashPass);
            entityManager.persist(user);
            return true;
        } catch (DaoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error saving user: {}", user, e);
            throw new DaoException("Error saving user: " + user, e);
        } finally {
            metricsService.stopQueryTimer(timerSample, "db.query.execution.time");
        }
    }

    public String generateUniqueUsername(String firstName, String lastName) {
        Optional<List<User>> optionalUsers = findAll();
        List<User> existingUsers = optionalUsers.orElse(Collections.emptyList());
        int suffix = 0;
        while (true) {
            String username = UserProfileUtil.generateUsername(firstName, lastName, suffix);
            if (existingUsers.stream().noneMatch(t -> t.getUsername().equals(username))) {
                return username;
            }
            suffix++;
        }
    }

    @Override
    public Optional<User> update(User updatedUser) {
        try {
            entityManager.merge(updatedUser);
            entityManager.flush();
            return Optional.of(updatedUser);
        } catch (Exception e) {
            log.error("Error updating user with id: {}", updatedUser.getUserId(), e);
            throw new DaoException("Error updating user with id " + updatedUser.getUserId(), e);
        }
    }

    @Override
    public boolean delete(User user) {
        try {
            entityManager.remove(user);
            return true;
        } catch (Exception e) {
            log.error("Error deleting user with id: {}", user.getUserId(), e);
            throw new DaoException("Error deleting user with id " + user.getUserId(), e);
        }
    }

    @Override
    public boolean deleteByUsername(String username) {
        try {
            Optional<User> optionalUser = findByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (!entityManager.contains(user)) {
                    user = entityManager.merge(user);
                }
                entityManager.remove(user);
                return true;
            } else {
                log.error("User with username {} not found", username);
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting user by username " + username, e);
            throw new DaoException("Error deleting user by username " + username, e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            User user = entityManager.find(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error("Error finding user with id " + id, e);
            throw new DaoException("Error finding user with id " + id, e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            String hql = "FROM User u WHERE u.username = :username";
            User user = entityManager.createQuery(hql, User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.ofNullable(user);
        } catch (NoResultException e) {
            log.warn("No user found with username: {}", username);
            throw new DaoException("No user found with username " + username, e);
        } catch (Exception e) {
            log.error("Error finding user with username " + username, e);
            throw new DaoException("Error finding user with username " + username, e);
        }
    }

    @Override
    public Optional<User> findByFirstAndLastName(String firstName, String lastName) {
        try {
            String hql = "FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName";
            List<User> users = entityManager.createQuery(hql, User.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .getResultList();
            if (users.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(users.get(0));
            }
        } catch (Exception e) {
            log.error("Error finding user with firstname " + firstName + " and lastname " + lastName, e);
            throw new DaoException("Error finding user with firstname " + firstName + " and lastname " + lastName, e);
        }
    }

    @Override
    public Optional<List<User>> findAll() {
        try {
            String hql = "FROM User";
            List<User> users = entityManager.createQuery(hql, User.class).getResultList();
            return Optional.ofNullable(users);
        } catch (Exception e) {
            log.error("Error finding all users", e);
            throw new DaoException("Error finding users", e);
        }
    }
}
