package com.example.crm_gym.dao;

import com.example.crm_gym.models.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDAO {
    boolean save(Training training);
    boolean update(int id, Training training);
    boolean delete(int id);
    Optional<Training> findById(int id);
    Optional<List<Training>> findAll();
}
