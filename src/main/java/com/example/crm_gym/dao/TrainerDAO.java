package com.example.crm_gym.dao;

import com.example.crm_gym.models.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDAO {
    boolean save(Trainer trainer);
    boolean update(int userId, Trainer trainer);
    boolean delete(int userId);
    Optional<Trainer> findById(int userId);
    Optional<List<Trainer>> findAll();
}
