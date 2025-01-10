package com.example.crm_gym.dtoConverter;

import com.example.crm_gym.dto.TrainingTypeDTO;
import com.example.crm_gym.models.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeConverter {

    public TrainingType convertToEntity(TrainingTypeDTO trainingTypeDTO) {
        if (trainingTypeDTO == null) {
            return null;
        }
        TrainingType trainingType = new TrainingType();
        trainingType.setId(trainingTypeDTO.getId());
        trainingType.setName(trainingTypeDTO.getName());
        return trainingType;
    }
}
