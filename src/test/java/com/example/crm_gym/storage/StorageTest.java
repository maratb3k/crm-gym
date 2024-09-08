package com.example.crm_gym.storage;

import static org.mockito.Mockito.*;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.example.crm_gym.config.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
public class StorageTest {

    @Mock
    private Map<Integer, Trainer> trainerStorage;

    @Mock
    private Map<Integer, Trainee> traineeStorage;

    @Mock
    private Map<Integer, Training> trainingStorage;

    @Autowired
    private Storage storage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storage = new Storage(trainerStorage, traineeStorage, trainingStorage);
    }

    @Test
    void testInit() throws IOException {
        BufferedReader bufferedReaderMock = Mockito.mock(BufferedReader.class);
        Mockito.lenient().when(bufferedReaderMock.readLine())
                .thenReturn("Trainer,{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"username\":\"johndoe\",\"password\":\"pass\",\"isActive\":true,\"specialization\":\"Yoga\"}")
                .thenReturn("Trainer,{\"id\":2,\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"username\":\"janesmith\",\"password\":\"pass\",\"isActive\":true,\"specialization\":\"Pilates\"}")
                .thenReturn("Trainee,{\"id\":1,\"firstName\":\"Emily\",\"lastName\":\"Johnson\",\"username\":\"emilyj\",\"password\":\"pass\",\"isActive\":true,\"dateOfBirth\":\"1990-01-01\",\"address\":\"123 Main St\"}")
                .thenReturn("Training,{\"id\":1,\"traineeId\":1,\"trainerId\":2,\"trainingName\":\"Yoga Session\",\"trainingType\":\"Yoga\",\"trainingDate\":\"2023-12-01\",\"trainingDuration\":\"1 hour\"}")
                .thenReturn(null);

        Mockito.mockConstruction(FileReader.class, (mock, context) -> {
        });

        Mockito.mockConstruction(BufferedReader.class, (mock, context) -> {
            Mockito.lenient().when(mock.readLine())
                    .thenReturn("Trainer,{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"username\":\"johndoe\",\"password\":\"pass\",\"isActive\":true,\"specialization\":\"Yoga\"}")
                    .thenReturn("Trainer,{\"id\":2,\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"username\":\"janesmith\",\"password\":\"pass\",\"isActive\":true,\"specialization\":\"Pilates\"}")
                    .thenReturn("Trainee,{\"id\":1,\"firstName\":\"Emily\",\"lastName\":\"Johnson\",\"username\":\"emilyj\",\"password\":\"pass\",\"isActive\":true,\"dateOfBirth\":\"1990-01-01\",\"address\":\"123 Main St\"}")
                    .thenReturn("Training,{\"id\":1,\"traineeId\":1,\"trainerId\":2,\"trainingName\":\"Yoga Session\",\"trainingType\":\"Yoga\",\"trainingDate\":\"2023-12-01\",\"trainingDuration\":\"1 hour\"}")
                    .thenReturn(null);
        });

        storage.init();

        verify(trainerStorage, times(2)).put(anyInt(), any(Trainer.class));
        verify(traineeStorage, times(1)).put(anyInt(), any(Trainee.class));
        verify(trainingStorage, times(1)).put(anyInt(), any(Training.class));
    }
}
