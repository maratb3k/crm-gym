package com.example.crm_gym.controllers;

import com.example.crm_gym.dto.*;
import com.example.crm_gym.logger.TransactionLogger;
import com.example.crm_gym.models.*;
import com.example.crm_gym.services.TrainerService;
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
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/trainers")
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting trainers in the application")
public class TrainerController {

    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping("/register")
    @ApiOperation(value = "Create a trainer", response = Trainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a trainer"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 409, message = "Conflict, trainer already exists"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Timed(value = "api.response.time", description = "API Response Time for Register Trainer")
    public ResponseEntity<Map<String, String>> registerTrainer(
            @Valid @RequestParam("firstName") String firstName,
            @Valid @RequestParam("lastName") String lastName,
            @RequestParam(name = "specializationId") Long specializationId,
            HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Trainer Registration");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        Optional<Trainer> trainer = trainerService.create(firstName, lastName, specializationId, transactionId);
        if (trainer.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("username", trainer.get().getUser().getUsername());
            response.put("password", trainer.get().getUser().getPassword());
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Trainer created successfully");
            TransactionLogger.logTransactionEnd(transactionId, "Trainer Registration");
            return ResponseEntity.ok(response);
        } else {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Error creating trainer");
            TransactionLogger.logTransactionEnd(transactionId, "Trainer Registration Failed");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error creating trainer.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Get a trainer", response = Trainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved a trainer"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 404, message = "Trainer not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getTrainerByUsername(@PathVariable String username, HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Get Trainer By Username");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        Optional<Trainer> optionalTrainer = trainerService.getTrainerByUsername(username, transactionId);
        if (optionalTrainer.isPresent()) {
            Trainer trainer = optionalTrainer.get();
            User user = trainer.getUser();
            UserDTO userDTO = new UserDTO(
                    user.getFirstName(),
                    user.getLastName(),
                    user.isActive()
            );
            List<TraineeDTO> traineeDTOs = trainer.getTrainees().stream()
                    .map(trainee -> new TraineeDTO(
                            new UserDTO(
                                    trainee.getUser().getUsername(),
                                    trainee.getUser().getFirstName(),
                                    trainee.getUser().getLastName()
                            )
                    )).collect(Collectors.toList());

            TrainerDTO trainerDTO = new TrainerDTO(
                    userDTO,
                    new TrainingTypeDTO(trainer.getSpecialization().getName())
            );
            trainerDTO.setTrainees(traineeDTOs);
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Trainer By Username retrieved successfully");
            TransactionLogger.logTransactionEnd(transactionId, "Get Trainer By Username");
            return ResponseEntity.ok(trainerDTO);
        } else {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Error retrieving trainer by username");
            TransactionLogger.logTransactionEnd(transactionId, "Get Trainer By Username Failed");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found");
        }
    }

    @PutMapping("/update")
    @ApiOperation(value = "Update a trainer", response = Trainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated the trainer"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 404, message = "Trainer not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity<?> updateTrainer(
            @Valid @RequestParam("username") String username,
            @Valid @RequestParam("firstName") String firstName,
            @Valid @RequestParam("lastName") String lastName,
            @RequestParam("specialization") TrainingType specialization,
            @RequestParam("isActive") Boolean isActive,
            HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Update Trainer");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        User newUser = new User(username, firstName, lastName, isActive);
        Trainer newTrainer = new Trainer(specialization, newUser);
        Optional<Trainer> optionalTrainer = trainerService.update(newTrainer, transactionId);

        if (optionalTrainer.isPresent()) {
            Trainer trainer = optionalTrainer.get();
            User user = trainer.getUser();
            UserDTO userDTO = new UserDTO(
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.isActive()
            );

            List<TraineeDTO> traineeDTOs = trainer.getTrainees().stream()
                    .map(trainee -> new TraineeDTO(
                            new UserDTO(
                                    trainee.getUser().getUsername(),
                                    trainee.getUser().getFirstName(),
                                    trainee.getUser().getLastName(),
                                    trainee.getUser().isActive()
                            )
                    )).collect(Collectors.toList());

            TrainerDTO responseDTO = new TrainerDTO(
                    userDTO,
                    new TrainingTypeDTO(trainer.getSpecialization().getName())
            );
            responseDTO.setTrainees(traineeDTOs);
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Trainer updated successfully");
            TransactionLogger.logTransactionEnd(transactionId, "Update Trainer");
            return ResponseEntity.ok(responseDTO);
        } else {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Error updating trainer");
            TransactionLogger.logTransactionEnd(transactionId, "Trainer Update Failed");
            log.error("Trainer with username {} not found", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found");
        }
    }

    @GetMapping("/trainings")
    @ApiOperation(value = "Get a trainer's trainings", response = Trainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the trainer's trainings."),
            @ApiResponse(code = 400, message = "Invalid input data."),
            @ApiResponse(code = 404, message = "No trainings found for the given criteria."),
            @ApiResponse(code = 500, message = "Application failed to process the request.")
    })
    public ResponseEntity<?> getTrainerTrainings(
            @RequestParam("username") String username,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(name = "traineeName", required = false) String traineeName,
            HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Get Trainer's Trainings");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        Optional<List<Training>> optionalTrainings = trainerService.getTrainingsByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName, transactionId);

        if (!optionalTrainings.isPresent() || optionalTrainings.get().isEmpty()) {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Error returning trainer's trainings");
            TransactionLogger.logTransactionEnd(transactionId, "Get Trainer's Trainings");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "No trainings found for the given criteria"));
        }

        List<TrainingDTO> trainingDTOList = optionalTrainings.get().stream().map(training -> {
            TraineeDTO traineeDTO = new TraineeDTO(
                    new UserDTO(
                            training.getTrainee().getUser().getUsername()
                    )
            );

            return new TrainingDTO(
                    training.getTrainingName(),
                    training.getTrainingDate(),
                    new TrainingTypeDTO(training.getTrainingType().getName()),
                    training.getTrainingDuration(),
                    traineeDTO
            );
        }).collect(Collectors.toList());
        TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Trainer's Trainings returned successfully");
        TransactionLogger.logTransactionEnd(transactionId, "Get Trainer's Trainings ");
        return ResponseEntity.ok(trainingDTOList);
    }

    @PatchMapping("/update/active")
    @ApiOperation(value = "Update trainer's isActive", response = Trainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainer activation status updated successfully."),
            @ApiResponse(code = 400, message = "Invalid input data."),
            @ApiResponse(code = 404, message = "Trainer not found."),
            @ApiResponse(code = 500, message = "Application failed to process the request.")
    })
    public ResponseEntity<?> activateTrainer(
            @RequestParam("username") String username,
            @RequestParam("isActive") Boolean isActive, HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Update Trainer's isActive");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());
        boolean updated = trainerService.updateTrainerActiveStatus(username, isActive, transactionId);
        if (updated) {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Trainer's isActive updated successfully");
            TransactionLogger.logTransactionEnd(transactionId, "Update Trainer's isActive");
            return ResponseEntity.ok().body("Trainer " + username + " has been " + (isActive ? "activated" : "deactivated"));
        } else {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Update trainer's isActive failed");
            TransactionLogger.logTransactionEnd(transactionId, "Update Trainer's isActive");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found");
        }
    }

    @DeleteMapping("/{username}")
    @ApiOperation(value = "Delete a trainer by username", response = Trainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted the trainer"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 404, message = "Trainer not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity<?> deleteTrainer(@PathVariable String username, HttpServletRequest request) {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Delete Trainer");
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());

        Optional<Trainer> optionalTrainer = trainerService.getTrainerByUsername(username, transactionId);
        if (optionalTrainer.isPresent()) {
            trainerService.delete(optionalTrainer.get());
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "getTrainerByUsername");
            TransactionLogger.logTransactionEnd(transactionId, "Delete Trainer");
            return ResponseEntity.ok().body("Trainer profile deleted successfully.");
        } else {
            TransactionLogger.logResponseDetails(transactionId, HttpStatus.NOT_FOUND.value(), "Error deleting trainer");
            TransactionLogger.logTransactionEnd(transactionId, "Deleting Trainer Failed");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found");
        }
    }

}
