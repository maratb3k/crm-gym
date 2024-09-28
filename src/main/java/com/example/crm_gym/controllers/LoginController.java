package com.example.crm_gym.controllers;

import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.models.User;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/user")
@Api(produces = "application/json", value = "Operations for login and updating user's password in the application")
public class LoginController {

    @Autowired
    private UserDAO userDao;

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        try {
            Optional<User> userOptional = userDao.findByUsername(username);
            if (!userOptional.isPresent()) {
                log.warn("Login failed for username: {}", username);
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid username or password.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            User user = userOptional.get();
            if (!user.getPassword().equals(password)) {
                log.warn("Invalid password for username: {}", username);
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid username or password.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during login for username: " + username, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/changepassword")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestParam("username") String username,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        try {
            Optional<User> userOptional = userDao.findByUsername(username);
            if (!userOptional.isPresent()) {
                log.warn("User not found for username: {}", username);
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid username.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            User user = userOptional.get();
            if (!user.getPassword().equals(oldPassword)) {
                log.warn("Old password does not match for username: {}", username);
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid old password.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            user.setPassword(newPassword);
            Optional<User> updatedUser = userDao.update(user);
            if (!updatedUser.isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to update password.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error changing password for username: " + username, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
