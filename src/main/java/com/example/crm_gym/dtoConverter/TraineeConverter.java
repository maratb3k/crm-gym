package com.example.crm_gym.dtoConverter;

import com.example.crm_gym.dto.TraineeDTO;
import com.example.crm_gym.dto.TrainerDTO;
import com.example.crm_gym.dto.TrainingTypeDTO;
import com.example.crm_gym.dto.UserDTO;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TraineeConverter implements Converter<TraineeDTO, Trainee> {

    @Override
    public TraineeDTO convertToDto(Trainee trainee) {
        if (trainee == null) {
            return null;
        }

        User user = trainee.getUser();
        UserDTO userDTO = new UserDTO(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.isActive()
        );

        List<TrainerDTO> trainerDTOs = trainee.getTrainers().stream()
                .map(trainer -> new TrainerDTO(
                        new UserDTO(
                                trainer.getUser().getUsername(),
                                trainer.getUser().getFirstName(),
                                trainer.getUser().getLastName(),
                                trainer.getUser().isActive()
                        ),
                        new TrainingTypeDTO(trainer.getSpecialization().getName())
                )).collect(Collectors.toList());

        TraineeDTO traineeDTO = new TraineeDTO(
                userDTO,
                trainee.getDateOfBirth(),
                trainee.getAddress()
        );
        traineeDTO.setTrainers(trainerDTOs);

        return traineeDTO;
    }

    @Override
    public Trainee convertToEntity(TraineeDTO dto) {
        if (dto == null) {
            return null;
        }

        UserDTO userDTO = dto.getUser();
        User user = new User(
                userDTO.getUsername(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.isActive()
        );

        Trainee trainee = new Trainee(
                dto.getDateOfBirth(),
                dto.getAddress(),
                user
        );

        return trainee;
    }

    @Override
    public List<TraineeDTO> convertModelListToDtoList(List<Trainee> entityList) {
        return entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
