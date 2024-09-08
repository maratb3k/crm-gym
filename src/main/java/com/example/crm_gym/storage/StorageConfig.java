package com.example.crm_gym.storage;

import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class StorageConfig {

    private static final Logger logger = LoggerFactory.getLogger(StorageConfig.class);

    @Bean(name = "trainerStorage")
    public Map<Integer, Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean(name = "traineeStorage")
    public Map<Integer, Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean(name = "trainingStorage")
    public Map<Integer, Training> trainingStorage() {
        return new HashMap<>();
    }

}
