package com.example.crm_gym.dtoConverter;

import com.example.crm_gym.dto.TrainerDTO;
import com.example.crm_gym.dto.TrainingDTO;
import com.example.crm_gym.dto.TrainingTypeDTO;
import com.example.crm_gym.dto.UserDTO;
import com.example.crm_gym.models.Training;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainingConverter implements Converter<TrainingDTO, Training> {

    @Override
    public TrainingDTO convertToDto(Training training) {
        if (training == null) {
            return null;
        }

        TrainerDTO trainerDTO = new TrainerDTO(
                new UserDTO(
                        training.getTrainer().getUser().getUsername(),
                        training.getTrainer().getUser().getFirstName(),
                        training.getTrainer().getUser().getLastName(),
                        training.getTrainer().getUser().isActive()
                ),
                new TrainingTypeDTO(training.getTrainingType().getName())
        );

        return new TrainingDTO(
                training.getTrainingName(),
                training.getTrainingDate(),
                new TrainingTypeDTO(training.getTrainingType().getName()),
                training.getTrainingDuration(),
                trainerDTO
        );
    }

    @Override
    public Training convertToEntity(TrainingDTO dto) {
        return null;
    }

    @Override
    public List<TrainingDTO> convertModelListToDtoList(List<Training> entityList) {
        return entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
