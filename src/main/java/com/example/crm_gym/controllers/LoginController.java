package com.example.crm_gym.controllers;

import com.example.crm_gym.services.UserService;
import com.example.crm_gym.utils.JwtUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
@Api(produces = "application/json", value = "Operations for login and updating user's password in the application")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        userService.authenticateUser(username, password);
        String token = jwtUtil.generateToken(username);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/changepassword")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> passwordData) {
        String username = passwordData.get("username");
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        userService.changePassword(username, oldPassword, newPassword);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully.");
        return ResponseEntity.ok(response);
    }
}
