package com.example.crm_gym.dao;

import com.example.crm_gym.models.Training;

import java.util.List;

public interface TrainingDAO {
    void save(Training training);
    void update(int id, Training training);
    void delete(int id);
    Training findById(int id);
    List<Training> findAll();
}
