package com.example.crm_gym.controllers;

import com.example.crm_gym.dto.*;
import com.example.crm_gym.logger.TransactionLogger;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.services.TraineeService;
import com.example.crm_gym.services.TrainerService;
import com.example.crm_gym.utils.JwtUtil;
import io.micrometer.core.annotation.Timed;
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

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/trainees")
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting trainees in the application")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final JwtUtil jwtUtil;

    @Autowired
    public TraineeController(TraineeService traineeService, TrainerService trainerService, JwtUtil jwtUtil) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    @ApiOperation(value = "Create a trainee", response = Trainee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a trainee"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 409, message = "Conflict, trainee already exists"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Timed(value = "api.response.time", description = "API Response Time for Register Trainee")
    public ResponseEntity<Map<String, String>> registerTrainee(
            @Valid @RequestParam("firstName") String firstName,
            @Valid @RequestParam("lastName") String lastName,
            @RequestParam(name = "dateOfBirth", required = false) Date dateOfBirth,
            @Valid @RequestParam(name = "address", required = false) String address,
            HttpServletRequest request) {

        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Trainee Registration");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        Optional<Trainee> trainee = traineeService.create(firstName, lastName, dateOfBirth, address, transactionId);
        if (trainee.isPresent()) {
            String token = jwtUtil.generateToken(trainee.get().getUser().getUsername());
            Map<String, String> response = new HashMap<>();
            response.put("username", trainee.get().getUser().getUsername());
            response.put("password", trainee.get().getUser().getPassword());
            response.put("token", "Bearer " + token);
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.CREATED.value(), "Trainee created successfully");
            TransactionLogger.logTransactionEnd(transactionId, "Trainee Registration");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.CONFLICT.value(), "Trainee already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "Trainee already exists."));
        }
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Get a trainee", response = Trainee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved a trainee"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 404, message = "Trainee not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getTraineeByUsername(@PathVariable String username, HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "getTraineeByUsername");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());
        Optional<TraineeDTO> traineeDTOOptional = traineeService.getTraineeByUsername(username, transactionId);

        if (traineeDTOOptional.isPresent()) {
            TraineeDTO traineeDTO = traineeDTOOptional.get();
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "getTraineeByUsername");
            TransactionLogger.logTransactionEnd(transactionId, "getTraineeByUsername");
            return ResponseEntity.ok(traineeDTO);
        } else {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Error retrieving trainee by username");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found");
        }
    }

    @PutMapping
    @ApiOperation(value = "Update a trainee", response = Trainee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated the trainee"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 404, message = "Trainee not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity<?> updateTrainee(
            @Valid @RequestParam("username") String username,
            @Valid @RequestParam("firstName") String firstName,
            @Valid @RequestParam("lastName") String lastName,
            @RequestParam(name = "dateOfBirth", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirth,
            @Valid @RequestParam(name = "address", required = false) String address,
            @RequestParam(name = "isActive") Boolean isActive,
            HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "updateTrainee");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        Optional<TraineeDTO> optionalTraineeDTO = traineeService.update(
                username, firstName, lastName, dateOfBirth, address, isActive, transactionId);

        if (optionalTraineeDTO.isPresent()) {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Trainee updated successfully");
            TransactionLogger.logTransactionEnd(transactionId, "Update Trainee");
            return ResponseEntity.ok(optionalTraineeDTO.get());
        } else {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Error updating trainee");
            TransactionLogger.logTransactionEnd(transactionId, "Updating Trainee Failed");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found");
        }
    }

    @DeleteMapping("/{username}")
    @ApiOperation(value = "Delete a trainee by username", response = Trainee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted the trainee"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 404, message = "Trainee not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity<?> deleteTrainee(@PathVariable String username, HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Delete Trainee");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        boolean isDeleted = traineeService.deleteTraineeByUsername(username, transactionId);
        if (isDeleted) {
            return ResponseEntity.ok().body("Trainee profile deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found.");
        }
    }

    @GetMapping("/trainings")
    @ApiOperation(value = "Get a trainee's trainings", response = Trainee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the trainee's trainings."),
            @ApiResponse(code = 400, message = "Invalid input data."),
            @ApiResponse(code = 404, message = "No trainings found for the given criteria."),
            @ApiResponse(code = 500, message = "Application failed to process the request.")
    })
    public ResponseEntity<?> getTraineeTrainings(
            @Valid @RequestParam("username") String username,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @Valid @RequestParam(name = "trainerName", required = false) String trainerName,
            @Valid @RequestParam(name = "trainingType", required = false) String trainingTypeName,
            HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "GetTrainee's Trainings");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        Optional<List<TrainingDTO>> optionalTrainings = traineeService.getTrainingsByTraineeUsernameAndCriteria(username,
                fromDate, toDate, trainerName, trainingTypeName, transactionId);

        if (!optionalTrainings.isPresent() || optionalTrainings.get().isEmpty()) {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "No trainings found");
            TransactionLogger.logTransactionEnd(transactionId, "Get Trainee Trainings Failed");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "No trainings found for the given criteria"));
        }

        TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Get Trainee Trainings Success");
        TransactionLogger.logTransactionEnd(transactionId, "Get Trainee Trainings");
        return ResponseEntity.ok(optionalTrainings.get());
    }

    @GetMapping("/trainers")
    @ApiOperation(value = "Get active trainers that not assigned to trainee", response = Trainee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the active trainers."),
            @ApiResponse(code = 400, message = "Invalid input data."),
            @ApiResponse(code = 404, message = "No active trainers found for the given criteria."),
            @ApiResponse(code = 500, message = "Application failed to process the request.")
    })
    public ResponseEntity<?> getTrainersNotAssignedToTrainee(
            @RequestParam("username") String traineeUsername,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Get Active Trainers Not Assigned To Trainee");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        Optional<List<TrainerDTO>> optionalTrainers = traineeService.findTrainersNotAssignedToTraineeByUsername(traineeUsername, isActive, transactionId);

        if (!optionalTrainers.isPresent() || optionalTrainers.get().isEmpty()) {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "No active trainers found for the given criteria");
            TransactionLogger.logTransactionEnd(transactionId, "GetTrainersNotAssignedToTrainee");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "No active trainers found for the given criteria"));
        }

        TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Active Trainers Not Assigned To Trainee retrieved");
        TransactionLogger.logTransactionEnd(transactionId, "GetTrainersNotAssignedToTrainee");
        return ResponseEntity.ok(optionalTrainers.get());
    }

    @PutMapping("/trainers")
    @ApiOperation(value = "Update trainee's trainers", response = Trainee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated the trainers."),
            @ApiResponse(code = 400, message = "Invalid input data."),
            @ApiResponse(code = 404, message = "Trainee or trainers not found."),
            @ApiResponse(code = 500, message = "Application failed to process the request.")
    })
    public ResponseEntity<?> updateTraineeTrainers(
            @RequestParam("username") String username,
            @RequestBody List<String> trainerUsernames,
            HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "update Trainee's Trainers");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        List<Trainer> trainers = trainerService.getTrainersByUsernames(trainerUsernames, transactionId);
        List<TrainerDTO> updatedTrainerDTOs = traineeService.updateTraineeTrainers(username, trainers, transactionId);

        TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Trainee's Trainers updated.");
        TransactionLogger.logTransactionEnd(transactionId, "updateTraineeTrainers");
        return ResponseEntity.ok(updatedTrainerDTOs);
    }

    @PatchMapping("/active")
    @ApiOperation(value = "Update trainee's isActive", response = Trainee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee activation status updated successfully."),
            @ApiResponse(code = 400, message = "Invalid input data."),
            @ApiResponse(code = 404, message = "Trainee not found."),
            @ApiResponse(code = 500, message = "Application failed to process the request.")
    })
    public ResponseEntity<?> activateTrainee(
            @RequestParam("username") String username,
            @RequestParam("isActive") Boolean isActive,
            HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Activate Trainee");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        boolean updated = traineeService.updateTraineeActiveStatus(username, isActive, transactionId);
        if (updated) {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Trainee activation updated.");
            TransactionLogger.logTransactionEnd(transactionId, "activateTrainee");
            return ResponseEntity.ok().body("Trainee " + username + " has been " + (isActive ? "activated" : "deactivated"));
        } else {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Error update isActive field of trainee");
            TransactionLogger.logTransactionEnd(transactionId, "activateTrainee");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found");
        }
    }

}
