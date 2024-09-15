package com.example.crm_gym.services;

import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.example.crm_gym.utils.UserProfileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
public class UserService {
    private UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean create(String firstName, String lastName) {
        try {
            String username = generateUniqueUsername(firstName, lastName);
            String password = UserProfileUtil.generatePassword();
            User user = new User(firstName, lastName, username, password, true);
            return userDAO.save(user);
        } catch (DaoException e) {
            log.error("Error creating user with firstName {} and lastName {}", firstName, lastName, e);
            return false;
        }
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        Optional<List<User>> optionalUsers = userDAO.findAll();
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

    public boolean update(String username, String password, Long userId, User user) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            return userDAO.update(userId, user);
        } catch (DaoException e) {
            log.error("Error updating user with id {}: {}", userId, e);
            return false;
        }
    }

    public boolean updatePassword(String username, String password, Long id, String newPassword) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }
            return userDAO.updatePassword(id, newPassword);
        } catch (DaoException e) {
            log.error("Error updating password for user with id {}: {}", id, e);
            return false;
        }
    }

    public boolean updateUserIsActive(String username, String password, Long id, boolean isActive) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }

            boolean result = userDAO.updateUserIsActive(id, isActive);
            if (!result) {
                log.error("Failed to update 'isActive' field for user with id: {}", id);
                return false;
            }
            return true;
        } catch (DaoException e) {
            log.error("Error updating 'isActive' field for user with id: {}", id, e);
            return false;
        }
    }

    public boolean delete(String username, String password, Long userId) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }

            return userDAO.delete(userId);
        } catch (DaoException e) {
            log.error("Error deleting user with id {}", userId, e);
            return false;
        }
    }

    public boolean deleteByUsername(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return false;
            }

            boolean result = userDAO.deleteByUsername(username);
            if (!result) {
                log.error("Failed to delete user with username: {}", username);
                return false;
            }
            return true;
        } catch (DaoException e) {
            log.error("Error deleting user with username: {}", username, e);
            return false;
        }
    }

    public Optional<User> getUserById(String username, String password, Long userId) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return userDAO.findById(userId);
        } catch (DaoException e) {
            log.error("Error fetching user with id {}", userId, e);
            return Optional.empty();
        }
    }

    public Optional<User> getByUsername(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Optional.empty();
            }
            return userDAO.findByUsername(username);
        } catch (DaoException e) {
            log.error("Error fetching user with username: {}", username, e);
            return Optional.empty();
        }
    }

    public List<User> getAllUsers(String username, String password) {
        try {
            if (!userDAO.checkUsernameAndPassword(username, password)) {
                log.error("Invalid credentials for user {}", username);
                return Collections.emptyList();
            }

            Optional<List<User>> users = userDAO.findAll();
            if (users.isPresent()) {
                return users.get();
            } else {
                log.warn("No users found.");
                return Collections.emptyList();
            }
        } catch (DaoException e) {
            log.error("Error fetching all users", e);
            return Collections.emptyList();
        }
    }



}
