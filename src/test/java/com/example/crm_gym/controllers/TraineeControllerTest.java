package com.example.crm_gym.controllers;

import com.example.crm_gym.models.*;
import com.example.crm_gym.services.TraineeService;
import com.example.crm_gym.services.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TraineeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TraineeController traineeController;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    private User user;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();

        User user = new User("John", "Doe", true);
        user.setUsername("John.Doe");
        Trainee trainee = new Trainee(new Date("1990-01-01"), "St.April, 123", user);
        trainee.setUser(user);
    }

    @Test
    void testRegisterTrainee() throws Exception {
        when(traineeService.create(eq("John"), eq("Doe"), any(Date.class), eq("St.April, 123"), anyString()))
                .thenReturn(Optional.of(trainee))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/trainees/register")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("address", "St.April, 123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.password").exists());

        mockMvc.perform(post("/trainees/register")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("address", "St.April, 123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Trainee already exists."));

        verify(traineeService, times(2)).create(eq("John"), eq("Doe"), any(Date.class), eq("St.April, 123"), anyString());
    }

    @Test
    void testGetTraineeByUsername() throws Exception {
        User trainerUser = new User("TrainerFirst", "TrainerLast", true);
        trainerUser.setUsername("TrainerFirst.TrainerLast");
        Trainer trainer = new Trainer(new TrainingType(TrainingTypeName.CARDIO), trainerUser);
        List<Trainer> trainers = Collections.singletonList(trainer);
        trainee.setTrainers(trainers);

        when(traineeService.getTraineeByUsername(eq("John.Doe"), anyString()))
                .thenReturn(Optional.of(trainee));

        mockMvc.perform(get("/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userDTO.firstName").value("John"))
                .andExpect(jsonPath("$.userDTO.lastName").value("Doe"))
                .andExpect(jsonPath("$.userDTO.active").value(true))
                .andExpect(jsonPath("$.dateOfBirth").exists())
                .andExpect(jsonPath("$.address").value("St.April, 123"))
                .andExpect(jsonPath("$.trainers[0].userDTO.username").value("TrainerFirst.TrainerLast"))
                .andExpect(jsonPath("$.trainers[0].userDTO.firstName").value("TrainerFirst"))
                .andExpect(jsonPath("$.trainers[0].userDTO.lastName").value("TrainerLast"))
                .andExpect(jsonPath("$.trainers[0].trainingTypeDTO.name").value(TrainingTypeName.CARDIO));

        verify(traineeService, times(1)).getTraineeByUsername(eq("John.Doe"), anyString());
    }

    @Test
    void testUpdateTrainee() throws Exception {
        User trainerUser = new User("TrainerFirst", "TrainerLast", true);
        trainerUser.setUsername("TrainerFirst.TrainerLast");
        Trainer trainer = new Trainer(new TrainingType(TrainingTypeName.CARDIO), trainerUser);
        List<Trainer> trainers = Collections.singletonList(trainer);
        trainee.setTrainers(trainers);

        when(traineeService.update(any(Trainee.class), anyString())).thenReturn(Optional.of(trainee));

        mockMvc.perform(put("/trainees/update")
                        .param("username", "John.Doe")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dateOfBirth", "1990-01-01")
                        .param("address", "123 Main St")
                        .param("isActive", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userDTO.username").value("John.Doe"))
                .andExpect(jsonPath("$.userDTO.firstName").value("John"))
                .andExpect(jsonPath("$.userDTO.lastName").value("Doe"))
                .andExpect(jsonPath("$.userDTO.active").value(true))
                .andExpect(jsonPath("$.address").value("St.April, 123"))
                .andExpect(jsonPath("$.trainers[0].userDTO.username").value("TrainerFirst.TrainerLast"))
                .andExpect(jsonPath("$.trainers[0].userDTO.firstName").value("TrainerFirst"))
                .andExpect(jsonPath("$.trainers[0].userDTO.lastName").value("TrainerLast"))
                .andExpect(jsonPath("$.trainers[0].trainingTypeDTO.name").value(TrainingTypeName.CARDIO));

        verify(traineeService, times(1)).update(any(Trainee.class), anyString());
    }

    @Test
    void testDeleteTrainee() throws Exception {
        when(traineeService.getTraineeByUsername(eq("John.Doe"), anyString()))
                .thenReturn(Optional.of(trainee))
                .thenReturn(Optional.empty());

        doNothing().when(traineeService).delete(any(Trainee.class));

        mockMvc.perform(delete("/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainee profile deleted successfully."));

        verify(traineeService, times(1)).getTraineeByUsername(eq("John.Doe"), anyString());
        verify(traineeService, times(1)).delete(any(Trainee.class));

        mockMvc.perform(delete("/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Trainee not found"));

        verify(traineeService, times(2)).getTraineeByUsername(eq("John.Doe"), anyString());
        verify(traineeService, times(1)).delete(any(Trainee.class));
    }

    @Test
    void testGetTraineeTrainings() throws Exception {
        List<Training> trainings = Arrays.asList(
                new Training(trainee, new Trainer(new User("Alex", "Smith")), "Strength Training", new TrainingType(TrainingTypeName.STRENGTH), new Date(), 60),
                new Training(trainee, new Trainer(new User("Jane", "Smith")), "Cardio Training", new TrainingType(TrainingTypeName.CARDIO), new Date(), 45)
        );

        when(traineeService.getTrainingsByTraineeUsernameAndCriteria(eq("John.Doe"), any(), any(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(trainings))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/trainees/trainings")
                        .param("username", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Strength Training"))
                .andExpect(jsonPath("$[0].trainingDate").exists())
                .andExpect(jsonPath("$[0].trainingType.name").value("STRENGTH"))
                .andExpect(jsonPath("$[0].trainingDuration").value(60))
                .andExpect(jsonPath("$[0].trainerDTO.userDTO.firstName").value("Alex"))
                .andExpect(jsonPath("$[0].trainerDTO.userDTO.lastName").value("Smith"))
                .andExpect(jsonPath("$[1].trainingName").value("Cardio Training"))
                .andExpect(jsonPath("$[1].trainingDate").exists())
                .andExpect(jsonPath("$[1].trainingType.name").value("CARDIO"))
                .andExpect(jsonPath("$[1].trainingDuration").value(45))
                .andExpect(jsonPath("$[1].trainerDTO.userDTO.firstName").value("Jane"))
                .andExpect(jsonPath("$[1].trainerDTO.userDTO.lastName").value("Smith"));

        verify(traineeService, times(1)).getTrainingsByTraineeUsernameAndCriteria(eq("John.Doe"), any(), any(), anyString(), anyString(), anyString());

        mockMvc.perform(get("/trainees/trainings")
                        .param("username", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No trainings found for the given criteria"));

        verify(traineeService, times(2)).getTrainingsByTraineeUsernameAndCriteria(eq("John.Doe"), any(), any(), anyString(), anyString(), anyString());
    }

    @Test
    void testGetActiveTrainersNotAssignedToTrainee() throws Exception {
        List<Trainer> trainers = Arrays.asList(
                new Trainer(new TrainingType(TrainingTypeName.STRENGTH), new User("Alex", "Smith", true)),
                new Trainer(new TrainingType(TrainingTypeName.CARDIO), new User("Jane", "Doe", true))
        );
        trainers.get(0).getUser().setUsername("Alex.Smith");
        trainers.get(1).getUser().setUsername("Jane.Doe");

        when(traineeService.findTrainersNotAssignedToTraineeByUsername(eq("John.Doe"), anyString()))
                .thenReturn(Optional.of(trainers))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/trainees/active-trainers")
                        .param("username", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userDTO.username").value("Alex.Smith"))
                .andExpect(jsonPath("$[0].userDTO.firstName").value("Alex"))
                .andExpect(jsonPath("$[0].userDTO.lastName").value("Smith"))
                .andExpect(jsonPath("$[0].trainingTypeDTO.name").value("Strength"))
                .andExpect(jsonPath("$[1].userDTO.username").value("Jane.Doe"))
                .andExpect(jsonPath("$[1].userDTO.firstName").value("Jane"))
                .andExpect(jsonPath("$[1].userDTO.lastName").value("Doe"))
                .andExpect(jsonPath("$[1].trainingTypeDTO.name").value("Cardio"));

        verify(traineeService, times(1)).findTrainersNotAssignedToTraineeByUsername(eq("John.Doe"), anyString());

        mockMvc.perform(get("/trainees/active-trainers")
                        .param("username", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No active trainers found for the given criteria"));

        verify(traineeService, times(2)).findTrainersNotAssignedToTraineeByUsername(eq("John.Doe"), anyString());
    }

    @Test
    void testUpdateTraineeTrainers() throws Exception {
        List<Trainer> trainers = Arrays.asList(
                new Trainer(new TrainingType(TrainingTypeName.STRENGTH), new User("Alex", "Smith", true)),
                new Trainer(new TrainingType(TrainingTypeName.CARDIO), new User("Jane", "Doe", true))
        );
        when(trainerService.getTrainersByUsernames(anyList(), anyString())).thenReturn(trainers);
        when(traineeService.updateTraineeTrainers(eq("John.Doe"), eq(trainers), anyString())).thenReturn(trainers);

        mockMvc.perform(put("/trainees/update-trainers")
                        .param("username", "John.Doe")
                        .content("[\"Alex.Smith\", \"Jane.Doe\"]")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userDTO.username").value("Alex.Smith"))
                .andExpect(jsonPath("$[0].userDTO.firstName").value("Alex"))
                .andExpect(jsonPath("$[0].userDTO.lastName").value("Smith"))
                .andExpect(jsonPath("$[0].trainingTypeDTO.name").value(TrainingTypeName.STRENGTH))
                .andExpect(jsonPath("$[1].userDTO.username").value("Jane.Doe"))
                .andExpect(jsonPath("$[1].userDTO.firstName").value("Jane"))
                .andExpect(jsonPath("$[1].userDTO.lastName").value("Doe"))
                .andExpect(jsonPath("$[1].trainingTypeDTO.name").value(TrainingTypeName.CARDIO));

        verify(trainerService, times(1)).getTrainersByUsernames(anyList(), anyString());
        verify(traineeService, times(1)).updateTraineeTrainers(eq("John.Doe"), eq(trainers), anyString());

        when(trainerService.getTrainersByUsernames(anyList(), anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(put("/trainees/update-trainers")
                        .param("username", "John.Doe")
                        .content("[\"trainer3\", \"trainer4\"]")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Trainee or trainers not found."));

        verify(trainerService, times(2)).getTrainersByUsernames(anyList(), anyString());
        verify(traineeService, times(1)).updateTraineeTrainers(eq("John.Doe"), eq(Collections.emptyList()), anyString());
    }

}