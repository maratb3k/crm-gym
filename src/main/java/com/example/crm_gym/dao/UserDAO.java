package com.example.crm_gym.dao;

import com.example.crm_gym.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<User> save(User user);
    Optional<User> update(User updatedUser);
    boolean delete(User user);
    boolean deleteByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<List<User>> findAll();
    boolean checkUsernameAndPassword(String username, String password);
}
