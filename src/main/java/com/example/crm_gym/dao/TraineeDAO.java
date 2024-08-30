package com.example.crm_gym.dao;

import com.example.crm_gym.models.Trainee;

import java.util.List;

public interface TraineeDAO {
    void save(Trainee trainee);
    void update(int userId, Trainee trainee);
    void delete(int userId);
    Trainee findById(int userId);
    List<Trainee> findAll();
}
