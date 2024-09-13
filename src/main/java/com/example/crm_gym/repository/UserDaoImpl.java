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
    public boolean save(User user) {
        try {
            entityManager.persist(user);
            return true;
        } catch (Exception e) {
            log.error("Error saving user: {}", user, e);
            return false;
        }
    }

    @Override
    public boolean update(Long id, User updatedUser) {
        try {
            User existingUser = entityManager.find(User.class, id);
            if (existingUser != null) {
                existingUser.setFirstName(updatedUser.getFirstName());
                existingUser.setLastName(updatedUser.getLastName());
                existingUser.setUsername(updatedUser.getUsername());
                existingUser.setPassword(updatedUser.getPassword());
                existingUser.setActive(updatedUser.isActive());
                entityManager.merge(existingUser);
                entityManager.flush();
                return true;
            } else {
                log.error("User with id {} not found.", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error updating user with id: {}", id, e);
            throw new DaoException("Error updating user with id " + id, e);
        }
    }

    @Override
    public boolean updatePassword(Long id, String newPassword) {
        try {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                user.setPassword(newPassword);
                entityManager.merge(user);
                return true;
            } else {
                log.error("Error updating user password. User with id: {} not found.", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error updating user password. User with id: {} not found.", id);
            throw new DaoException("Error updating user password with id " + id, e);
        }
    }

    @Override
    public boolean updateUserIsActive(Long id, boolean isActive) {
        try {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                user.setActive(isActive);
                entityManager.merge(user);
                return true;
            } else {
                log.error("Error updating user 'isActive' field. User with id: {} not found.", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error updating user 'isActive' field. User with id: {} not found.", id);
            throw new DaoException("Error updating user 'isActive' field with id " + id, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            String hql = "DELETE FROM User u WHERE u.userId = :id";
            int deletedCount = entityManager.createQuery(hql)
                    .setParameter("id", id)
                    .executeUpdate();
            if (deletedCount > 0) {
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting user with id: {}", id);
            throw new DaoException("Error deleting user with id " + id, e);
        }
        return false;
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
            return Optional.empty();
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
        } catch (Exception e) {
            log.error("Error checking username and password", e);
            throw new DaoException("Error checking username and password. Username: " + username, e);
        }
    }
}
