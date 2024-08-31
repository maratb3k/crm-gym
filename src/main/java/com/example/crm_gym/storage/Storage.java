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

    private String dataFilePath = "src/main/resources/initial_data.csv";

    private final ObjectMapper objectMapper = new ObjectMapper();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public Storage(Map<Integer, Trainer> trainerStorage,
                              Map<Integer, Trainee> traineeStorage,
                              Map<Integer, Training> trainingStorage) {
        this.trainerStorage = trainerStorage;
        this.traineeStorage = traineeStorage;
        this.trainingStorage = trainingStorage;
        logger.info("Storage initialized with storages for trainers, trainees, and trainings.");
    }


    @PostConstruct
    public void init() {
        logger.info("Entering init() method to load data from file: {}", dataFilePath);

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
                        logger.info("Trainer loaded: {}", trainer);
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
                        logger.info("Trainee loaded: {}", trainee);
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
                        logger.info("Training loaded: {}", training);
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
        } finally {
            logger.info("Exiting init() method");
        }
    }

    public Map<Integer, Trainee> getTrainees() {
        logger.info("Fetching all trainees.");
        return traineeStorage;
    }

    public Map<Integer, Trainer> getTrainers() {
        logger.info("Fetching all trainers.");
        return trainerStorage;
    }

    public Map<Integer, Training> getTrainings() {
        logger.info("Fetching all trainings.");
        return trainingStorage;
    }

    public <T> void save(Map<Integer, T> entityMap, int key, T entity) {
        logger.info("Entering save() with key: {} and entity: {}", key, entity);
        entityMap.put(key, entity);
        logger.info("Entity saved successfully with key: {}", key);
        logger.info("Exiting save() with key: {}", key);
    }

    public <T> void remove(Map<Integer, T> entityMap, int key) {
        logger.info("Entering remove() with key: {}", key);
        entityMap.remove(key);
        logger.info("Entity removed successfully with key: {}", key);
        logger.info("Exiting remove() with key: {}", key);
    }

    public <T> T get(Map<Integer, T> entityMap, int key) {
        logger.info("Entering get() with key: {}", key);
        T entity = entityMap.get(key);
        if (entity != null) {
            logger.info("Entity found with key: {}", key);
        } else {
            logger.warn("No entity found with key: {}", key);
        }
        logger.info("Exiting get() with key: {}", key);
        return entity;
    }

    public <T> void update(Map<Integer, T> entityMap, int key, T newEntity) {
        logger.info("Entering update() with key: {} and new entity: {}", key, newEntity);
        if (entityMap.containsKey(key)) {
            entityMap.put(key, newEntity);
            logger.info("Entity updated successfully with key: {}", key);
        } else {
            logger.warn("Entity with key {} does not exist. Cannot update.", key);
            throw new IllegalArgumentException("Entity with key " + key + " does not exist.");
        }
        logger.info("Exiting update() with key: {}", key);
    }

    public <T> List<T> findAll(Map<Integer, T> entityMap) {
        logger.info("Entering findAll()");
        List<T> entities = entityMap.values().stream().collect(Collectors.toList());
        logger.info("Found all entities, count: {}", entities.size());
        logger.info("Exiting findAll()");
        return entities;
    }

}
