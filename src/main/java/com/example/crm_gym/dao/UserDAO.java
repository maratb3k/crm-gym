package com.example.crm_gym.dao;

import com.example.crm_gym.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    boolean save(User user);
    boolean update(Long id, User user);
    boolean updatePassword(Long id, String newPassword);
    boolean updateUserIsActive(Long id, boolean isActive);
    boolean delete(Long id);
    boolean deleteByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<List<User>> findAll();
    boolean checkUsernameAndPassword(String username, String password);
}
