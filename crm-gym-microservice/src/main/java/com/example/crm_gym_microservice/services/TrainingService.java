package com.example.crm_gym_microservice.services;

import com.example.crm_gym_microservice.models.*;
import com.example.crm_gym_microservice.repositories.TrainerWorkloadRepository;
import com.example.crm_gym_microservice.repositories.TrainingSessionRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TrainingService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final TrainingSessionRepository trainingSessionRepository;

    public TrainingService(TrainerWorkloadRepository trainerWorkloadRepository, TrainingSessionRepository trainingSessionRepository) {
        this.trainerWorkloadRepository = trainerWorkloadRepository;
        this.trainingSessionRepository = trainingSessionRepository;
    }

    public void handleTrainingSession(TrainingSession trainingSession) {
    TrainerWorkload workload = trainerWorkloadRepository
            .findByTrainerUsername(trainingSession.getTrainerUsername())
            .orElse(TrainerWorkload.builder()
                    .trainerUsername(trainingSession.getTrainerUsername())
                    .trainerFirstName(trainingSession.getTrainerFirstName())
                    .trainerLastName(trainingSession.getTrainerLastName())
                    .isActive(trainingSession.getIsActive())
                    .years(new ArrayList<>())
                    .build());

    YearWorkload yearWorkload = workload.getYears().stream()
            .filter(y -> y.getTrainingYear() == trainingSession.getTrainingDate().getYear())
            .findFirst()
            .orElseGet(() -> {
                YearWorkload newYearWorkload = YearWorkload.builder()
                        .trainingYear(trainingSession.getTrainingDate().getYear())
                        .trainerWorkload(workload)
                        .months(new ArrayList<>())
                        .build();
                workload.getYears().add(newYearWorkload);
                return newYearWorkload;
            });

    MonthWorkload monthWorkload = yearWorkload.getMonths().stream()
            .filter(m -> m.getTrainingMonth() == trainingSession.getTrainingDate().getMonthValue())
            .findFirst()
            .orElseGet(() -> {
                MonthWorkload newMonthWorkload = MonthWorkload.builder()
                        .trainingMonth(trainingSession.getTrainingDate().getMonthValue())
                        .yearWorkload(yearWorkload)
                        .trainingSummaryDuration(0.0)
                        .build();
                yearWorkload.getMonths().add(newMonthWorkload);
                return newMonthWorkload;
            });

    if (trainingSession.getActionType() == ActionType.ADD) {
        monthWorkload.setTrainingSummaryDuration(monthWorkload.getTrainingSummaryDuration() + trainingSession.getTrainingDuration());
    } else if (trainingSession.getActionType() == ActionType.DELETE) {
        monthWorkload.setTrainingSummaryDuration(monthWorkload.getTrainingSummaryDuration() - trainingSession.getTrainingDuration());
    }
    trainerWorkloadRepository.save(workload);
}

    public double getMonthlyTrainingDuration(String trainerUsername, int year, int month) {
        return trainerWorkloadRepository.findTotalTrainingDuration(trainerUsername, year, month);
    }

    private void addUniqueYearAndMonth(YearWorkload yearWorkload, MonthWorkload monthWorkload) {
        if (!yearWorkload.getMonths().contains(monthWorkload)) {
            yearWorkload.getMonths().add(monthWorkload);
        }
    }
}
