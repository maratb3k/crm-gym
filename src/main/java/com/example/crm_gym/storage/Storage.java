package com.example.crm_gym.storage;

import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.TrainingType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Component
public class Storage {
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);

    private final Map<Integer, Trainer> trainerStorage;
    private final Map<Integer, Trainee> traineeStorage;
    private final Map<Integer, Training> trainingStorage;

    @Value("${storage.file}")
    private String dataFilePath;

    private final ObjectMapper objectMapper = new ObjectMapper();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public Storage(Map<Integer, Trainer> trainerStorage,
                   Map<Integer, Trainee> traineeStorage,
                   Map<Integer, Training> trainingStorage) {
        this.trainerStorage = trainerStorage;
        this.traineeStorage = traineeStorage;
        this.trainingStorage = trainingStorage;
    }

    @PostConstruct
    public void init() {
        try (BufferedReader br = new BufferedReader(new FileReader(dataFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 2);
                String entityType = data[0].trim();
                String jsonData = data[1].trim();
                JsonNode jsonNode = objectMapper.readTree(jsonData);

                switch (entityType) {
                    case "Trainer":
                        Trainer trainer = new Trainer(
                                jsonNode.get("id").asInt(),
                                jsonNode.get("firstName").asText(),
                                jsonNode.get("lastName").asText(),
                                jsonNode.get("username").asText(),
                                jsonNode.get("password").asText(),
                                jsonNode.get("isActive").asBoolean(),
                                jsonNode.get("specialization").asText()
                        );
                        trainerStorage.put(trainer.getUserId(), trainer);
                        break;

                    case "Trainee":
                        Trainee trainee = new Trainee(
                                jsonNode.get("id").asInt(),
                                jsonNode.get("firstName").asText(),
                                jsonNode.get("lastName").asText(),
                                jsonNode.get("username").asText(),
                                jsonNode.get("password").asText(),
                                jsonNode.get("isActive").asBoolean(),
                                formatter.parse(jsonNode.get("dateOfBirth").asText()),
                                jsonNode.get("address").asText()
                        );
                        traineeStorage.put(trainee.getUserId(), trainee);
                        break;

                    case "Training":
                        TrainingType trainingType = TrainingType.fromString(jsonNode.get("trainingType").asText());
                        Training training = new Training(
                                jsonNode.get("id").asInt(),
                                jsonNode.get("traineeId").asInt(),
                                jsonNode.get("trainerId").asInt(),
                                jsonNode.get("trainingName").asText(),
                                trainingType,
                                formatter.parse(jsonNode.get("trainingDate").asText()),
                                jsonNode.get("trainingDuration").asText()
                        );
                        trainingStorage.put(training.getId(), training);
                        break;
                    default:
                        logger.warn("Unknown entity type: {}", entityType);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading data file: {}", dataFilePath, e);
        } catch (ParseException e) {
            logger.error("Error parsing date in data file: {}", dataFilePath, e);
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, Trainee> getTrainees() {
        return traineeStorage;
    }

    public Map<Integer, Trainer> getTrainers() {
        return trainerStorage;
    }

    public Map<Integer, Training> getTrainings() {
        return trainingStorage;
    }

    public <T> void save(Map<Integer, T> entityMap, int key, T entity) {
        entityMap.put(key, entity);
    }

    public <T> void remove(Map<Integer, T> entityMap, int key) {
        entityMap.remove(key);
    }

    public <T> T get(Map<Integer, T> entityMap, int key) {
        T entity = entityMap.get(key);
        if (entity == null) {
            logger.warn("No entity found with key: {}", key);
        }
        return entity;
    }

    public <T> void update(Map<Integer, T> entityMap, int key, T newEntity) {
        if (entityMap.containsKey(key)) {
            entityMap.put(key, newEntity);
        } else {
            logger.warn("Entity with key {} does not exist. Cannot update.", key);
            throw new IllegalArgumentException("Entity with key " + key + " does not exist.");
        }
    }

    public <T> List<T> findAll(Map<Integer, T> entityMap) {
        return entityMap.values().stream().collect(Collectors.toList());
    }

}
