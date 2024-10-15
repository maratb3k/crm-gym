package com.example.crm_gym.dtoConverter;

import com.example.crm_gym.dto.TrainerDTO;
import com.example.crm_gym.dto.TrainingTypeDTO;
import com.example.crm_gym.dto.UserDTO;
import com.example.crm_gym.models.Trainer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainerConverter implements Converter<TrainerDTO, Trainer> {

    @Override
    public TrainerDTO convertToDto(Trainer trainer) {
        if (trainer == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().isActive()
        );

        TrainingTypeDTO trainingTypeDTO = new TrainingTypeDTO(trainer.getSpecialization().getName());

        return new TrainerDTO(userDTO, trainingTypeDTO);
    }

    @Override
    public Trainer convertToEntity(TrainerDTO dto) {
        return null;
    }

    @Override
    public List<TrainerDTO> convertModelListToDtoList(List<Trainer> entityList) {
        return entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
