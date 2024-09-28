package com.example.crm_gym.services;

import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.logger.TransactionLogger;
import com.example.crm_gym.models.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
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

    public boolean authenticateUser(String username, String password) {
        String transactionId = TransactionLogger.generateTransactionId();
        try {
            Optional<User> userOptional = userDAO.findByUsername(username);
            if (!userOptional.isPresent()) {
                log.warn("[Transaction ID: {}] - Authentication failed: User not found for username: {}", transactionId, username);
                return false;
            }

            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return true;
            } else {
                log.warn("[Transaction ID: {}] - Authentication failed: Invalid password for username: {}", transactionId, username);
                return false;
            }
        } catch (Exception e) {
            log.error("[Transaction ID: {}] - Error during authentication for username: {}", transactionId ,username, e);
            throw new ServiceException("Error during authentication for username: " + username, e);
        }
    }

}
