package com.example.crm_gym.controllers;

import com.example.crm_gym.logger.TransactionLogger;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.services.TraineeService;
import com.example.crm_gym.services.TrainerService;
import com.example.crm_gym.services.TrainingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/trainings")
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting trainings in the application")
public class TrainingController {
    private final TrainingService trainingService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    @Autowired
    public TrainingController(TrainingService trainingService, TraineeService traineeService, TrainerService trainerService) {
        this.trainingService = trainingService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @PostMapping("/create")
    @ApiOperation(value = "Create a training", response = Trainee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a training"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 409, message = "Conflict, training already exists"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, String>> createTraining(
            @Valid @RequestParam("traineeUsername") String traineeUsername,
            @Valid @RequestParam("trainerUsername") String trainerUsername,
            @Valid @RequestParam("trainingName") String trainingName,
            @Valid @RequestParam("trainingDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date trainingDate,
            @Valid @RequestParam("trainingDuration") int trainingDuration,
            HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Create Training");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        Optional<Trainee> optionalTrainee = traineeService.getTraineeByUsername(traineeUsername, transactionId);
        if (!optionalTrainee.isPresent()) {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Error creating trainer");
            TransactionLogger.logTransactionEnd(transactionId, "Trainer Registration Failed");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Trainee not found"));
        }

        Optional<Trainer> optionalTrainer = trainerService.getTrainerByUsername(trainerUsername, transactionId);
        if (!optionalTrainer.isPresent()) {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Error creating training");
            TransactionLogger.logTransactionEnd(transactionId, "Create Training");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Trainer not found"));
        }

        Trainee trainee = optionalTrainee.get();
        Trainer trainer = optionalTrainer.get();
        Training newTraining = new Training(trainee, trainer, trainingName, trainingDate, trainingDuration);
        trainingService.create(newTraining);
        TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Creating training");
        TransactionLogger.logTransactionEnd(transactionId, "Create Training");
        return ResponseEntity.ok(Collections.singletonMap("status", "Training successfully created"));
    }

}
