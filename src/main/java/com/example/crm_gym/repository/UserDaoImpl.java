package com.example.crm_gym.repository;

import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserDaoImpl implements UserDAO {

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public UserDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<User> save(User user) {
        try {
            entityManager.persist(user);
            return Optional.of(user);
        } catch (Exception e) {
            log.error("Error saving user: {}", user, e);
            throw new DaoException("Error saving user: " + user, e);
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
                throw new DaoException("User with username " + username + " not found");
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

    @Override
    public boolean checkUsernameAndPassword(String username, String password) {
        try {
            String jpql = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password";
            User user = entityManager.createQuery(jpql, User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
            if (user == null) {
                log.error("User with username {} not found or wrong password.", username);
                return false;
            }
            return true;
        } catch (NoResultException e) {
            log.warn("user not found with username: {}", username, e);
            throw new DaoException("user not found with username: " + username, e);
        } catch (Exception e) {
            log.error("Error checking username and password", e);
            throw new DaoException("Error checking username and password. Username: " + username, e);
        }
    }
}
