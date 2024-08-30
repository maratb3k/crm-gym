package com.example.crm_gym.dao;

import com.example.crm_gym.models.Trainer;

import java.util.List;

public interface TrainerDAO {
    void save(Trainer trainer);
    void update(int userId, Trainer trainer);
    void delete(int userId);
    Trainer findById(int userId);
    List<Trainer> findAll();
}
