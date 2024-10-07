package com.example.crm_gym.services;

import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.EntityNotFoundException;
import com.example.crm_gym.exception.InvalidCredentialsException;
import com.example.crm_gym.logger.TransactionLogger;
import com.example.crm_gym.models.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.example.crm_gym.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Optional<User> authenticateUser(String username, String password) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Optional<User> userOptional = userDAO.findByUsername(username);
            if (!userOptional.isPresent()) {
                log.warn("[Transaction ID: {}] - Authentication failed: User not found for username: {}", transactionId, username);
                throw new EntityNotFoundException("User with username " + username + " not found.");
            }
            User user = userOptional.get();
            if (!user.getPassword().equals(password)) {
                log.warn("[Transaction ID: {}] - Authentication failed: Invalid password for username: {}", transactionId, username);
                throw new InvalidCredentialsException("Invalid password for username: " + username);
            }
            return Optional.of(user);
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error during authentication for username: {}", transactionId ,username, e);
            throw new ServiceException("Error during authentication for username: " + username, e);
        }
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Optional<User> userOptional = authenticateUser(username, oldPassword);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setPassword(newPassword);
                Optional<User> updatedUser = userDAO.update(user);
                if (!updatedUser.isPresent()) {
                    log.warn("[Transaction ID: {}] - User password not updated: {}", transactionId, username);
                    throw new ServiceException("Failed to update password for username: " + username);
                }
            }
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error changing password for username: {}", transactionId, username, e);
            throw new ServiceException("Error occurred while changing password for username: " + username, e);
        }
    }
}
