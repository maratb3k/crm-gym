package com.example.crm_gym_microservice.controller;

import com.example.crm_gym_microservice.dtos.*;
import com.example.crm_gym_microservice.logger.TransactionLogger;
import com.example.crm_gym_microservice.models.TrainerWorkload;
import com.example.crm_gym_microservice.models.TrainingSession;
import com.example.crm_gym_microservice.services.TrainingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainings")
public class TrainingController {
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<Void> handleTrainingSession(@RequestBody TrainingSessionRequest request, HttpServletRequest httpRequest) {
        String transactionId = httpRequest.getHeader("TransactionID");
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = TransactionLogger.generateTransactionId();
        }
        TransactionLogger.logRequestDetails(transactionId, httpRequest.getMethod(), httpRequest.getRequestURI(), httpRequest.getParameterMap());
        TrainingSession trainingSession = mapToTrainingSession(request);
        trainingService.handleTrainingSession(trainingSession);
        TransactionLogger.logTransactionEnd(transactionId, "Training session handled successfully");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/{year}/{month}")
    public ResponseEntity<Double> getTrainerMonthlyDuration(
            @PathVariable String username, @PathVariable int year, @PathVariable int month, HttpServletRequest request) {
        String transactionId = request.getHeader("TransactionID");
        if (transactionId == null) {
            transactionId = TransactionLogger.generateTransactionId();
        }
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());
        double totalDuration = trainingService.getMonthlyTrainingDuration(username, year, month);
        return ResponseEntity.ok(totalDuration);
    }

    private TrainingSession mapToTrainingSession(TrainingSessionRequest request) {
        return TrainingSession.builder()
                .trainerUsername(request.trainerUsername())
                .trainerFirstName(request.trainerFirstName())
                .trainerLastName(request.trainerLastName())
                .isActive(request.isActive())
                .trainingDate(request.trainingDate())
                .trainingDuration(request.trainingDuration())
                .actionType(request.actionType())
                .build();
    }

    private TrainerWorkloadResponse mapToTrainerWorkloadResponse(TrainerWorkload workload) {
        return new TrainerWorkloadResponse(
                workload.getTrainerUsername(),
                workload.getTrainerFirstName(),
                workload.getTrainerLastName(),
                workload.getIsActive(),
                workload.getYears().stream()
                        .map(y -> new YearResponse(
                                y.getTrainingYear(),
                                y.getMonths().stream()
                                        .map(m -> new MonthResponse(m.getTrainingMonth(), m.getTrainingSummaryDuration()))
                                        .toList()))
                        .toList());
    }
}
