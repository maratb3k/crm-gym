package com.example.crm_gym.controllers;

import com.example.crm_gym.logger.TransactionLogger;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.TrainingType;
import com.example.crm_gym.services.TrainingTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/training-types")
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting training types in the application")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @Autowired
    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @GetMapping
    @ApiOperation(value = "Get training types", response = Trainee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved training types"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllTrainingTypes() {
        String transactionId = TransactionLogger.generateTransactionId();
        TransactionLogger.logTransactionStart(transactionId, "Get Training Types");
        List<TrainingType> trainingTypes = trainingTypeService.getAllTrainingTypes();
        List<Map<String, Object>> response = trainingTypes.stream().map(type -> {
            Map<String, Object> typeData = new HashMap<>();
            typeData.put("id", type.getId());
            typeData.put("name", type.getName().name());
            return typeData;
        }).collect(Collectors.toList());
        TransactionLogger.logResponseDetails(transactionId, HttpStatus.OK.value(), "Retrieving training types");
        TransactionLogger.logTransactionEnd(transactionId, "Get All Training type");
        return ResponseEntity.ok(response);
    }
}
