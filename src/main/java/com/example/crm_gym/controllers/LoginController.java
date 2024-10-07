package com.example.crm_gym.controllers;

import com.example.crm_gym.services.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
@Api(produces = "application/json", value = "Operations for login and updating user's password in the application")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        userService.authenticateUser(username, password);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/changepassword")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestParam("username") String username,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {

        userService.changePassword(username, oldPassword, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully.");
        return ResponseEntity.ok(response);
    }
}
