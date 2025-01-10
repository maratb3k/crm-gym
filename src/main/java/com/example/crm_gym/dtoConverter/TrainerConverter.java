package com.example.crm_gym.dtoConverter;

import com.example.crm_gym.dto.TrainerDTO;
import com.example.crm_gym.dto.TrainingTypeDTO;
import com.example.crm_gym.dto.UserDTO;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainerConverter implements Converter<TrainerDTO, Trainer> {

    @Autowired
    private TrainingTypeConverter trainingTypeConverter;

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
    public Trainer convertToEntity(TrainerDTO trainerDTO) {
        if (trainerDTO == null) {
            return null;
        }

        Trainer trainer = new Trainer();
        trainer.setId(trainerDTO.getId());
        trainer.setSpecialization(trainingTypeConverter.convertToEntity(trainerDTO.getSpecialization()));

        User user = new User();
        user.setUserId(trainerDTO.getUser().getUserId());
        user.setUsername(trainerDTO.getUser().getUsername());
        user.setFirstName(trainerDTO.getUser().getFirstName());
        user.setLastName(trainerDTO.getUser().getLastName());
        user.setPassword(trainerDTO.getUser().getPassword() != null ? trainerDTO.getUser().getPassword() : "defaultPassword");
        user.setActive(trainerDTO.getUser().isActive());

        trainer.setUser(user);
        return trainer;
    }



    @Override
    public List<TrainerDTO> convertModelListToDtoList(List<Trainer> entityList) {
        return entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
