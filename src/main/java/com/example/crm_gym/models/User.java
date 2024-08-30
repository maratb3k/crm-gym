package com.example.crm_gym.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
}
