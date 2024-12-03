package com.example.crm_gym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TraineeDTO {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    private String address;
    private UserDTO user;
    private List<TrainerDTO> trainers;
    private List<TrainingDTO> trainings;

    public TraineeDTO() {

    }

    public TraineeDTO(UserDTO user) {
        this.user = user;
    }

    public TraineeDTO(UserDTO user, Date dateOfBirth, String address) {
        this.user = user;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public TraineeDTO(Long id, UserDTO user, Date dateOfBirth, String address) {
        this.id = id;
        this.user = user;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public List<TrainerDTO> getTrainers() { return trainers; }
    public void setTrainers(List<TrainerDTO> trainers) { this.trainers = trainers; }

    public List<TrainingDTO> getTrainings() { return trainings; }
    public void setTrainings(List<TrainingDTO> trainings) { this.trainings = trainings; }
}
