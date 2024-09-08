package com.example.crm_gym.dao;

import com.example.crm_gym.models.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDAO {
    boolean save(Trainee trainee);
    boolean update(int userId, Trainee trainee);
    boolean delete(int userId);
    Optional<Trainee> findById(int userId);
    Optional<List<Trainee>> findAll();
}
