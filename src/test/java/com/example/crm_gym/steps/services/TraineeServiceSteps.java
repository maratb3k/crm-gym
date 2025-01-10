package com.example.crm_gym.steps.services;

import com.example.crm_gym.dto.TraineeDTO;
import com.example.crm_gym.dto.TrainerDTO;
import com.example.crm_gym.dto.TrainingDTO;
import com.example.crm_gym.dtoConverter.TraineeConverter;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.models.*;
import com.example.crm_gym.services.TraineeService;
import com.example.crm_gym.services.TrainerService;
import com.example.crm_gym.services.TrainingTypeService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
public class TraineeServiceSteps {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainingTypeService trainingTypeService;

    @Autowired
    private TrainerService trainerService;

    private TraineeDTO savedTrainee;
    private Optional<TraineeDTO> foundTrainee;
    private List<TraineeDTO> allTrainees;

    @Autowired
    private TraineeConverter traineeConverter;

    @Given("a new trainee with first name {string}, last name {string}, and date of birth {string}")
    public void a_new_trainee_with_first_name_last_name_and_date_of_birth(String firstName, String lastName, String dob) throws Exception {
        Date dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
        String username = firstName + "." + lastName;

        Trainee trainee = traineeService.create(firstName, lastName, dateOfBirth, "Valid Address Example", "txn123")
                .orElseThrow(() -> new RuntimeException("Trainee creation failed"));

        trainee.getUser().setUsername(username);
        savedTrainee = traineeConverter.convertToDto(trainee);
    }


    @When("the trainee is created")
    public void the_trainee_is_created() {
        Assertions.assertNotNull(savedTrainee, "Trainee should be created");
    }

    @Then("the trainee should be successfully created in the database")
    public void the_trainee_should_be_successfully_created_in_the_database() {
        Optional<TraineeDTO> result = traineeService.getTraineeByUsername(savedTrainee.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result.isPresent(), "Trainee should be found in the database");
    }

    @Given("a trainee with username {string} exists")
    public void a_trainee_with_username_exists(String username) throws Exception {
        String[] parts = username.split("\\.");
        String uniqueFirstName = parts[0] + System.currentTimeMillis();
        String lastName = parts[1];

        a_new_trainee_with_first_name_last_name_and_date_of_birth(uniqueFirstName, lastName, "1990-01-01");
    }


    @When("the trainee updates their profile with new first name {string}, new last name {string}, and address {string}")
    public void the_trainee_updates_their_profile_with_new_first_name_new_last_name_and_address(String firstName, String lastName, String address) {
        traineeService.update(savedTrainee.getUser().getUsername(), firstName, lastName, savedTrainee.getDateOfBirth(), address, true, "txn123");
    }

    @Then("the trainee's profile should be updated in the database")
    public void the_trainee_s_profile_should_be_updated_in_the_database() {
        Optional<TraineeDTO> result = traineeService.getTraineeByUsername(savedTrainee.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result.isPresent(), "Trainee should be found in the database");
        Assertions.assertEquals("Johnny", result.get().getUser().getFirstName(), "First name should be updated");
    }

    @When("the trainee is deleted")
    public void the_trainee_is_deleted() {
        boolean result = traineeService.deleteTraineeByUsername(savedTrainee.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result, "Trainee should be deleted");
    }

    @Then("the trainee should no longer exist in the database")
    public void the_trainee_should_no_longer_exist_in_the_database() {
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            traineeService.getTraineeByUsername(savedTrainee.getUser().getUsername(), "txn123");
        });

        Assertions.assertEquals("Trainee with username John.Doe not found", exception.getMessage());
    }


    @When("the trainee is activated")
    public void the_trainee_is_activated() {
        boolean result = traineeService.updateTraineeActiveStatus(savedTrainee.getUser().getUsername(), true, "txn123");
        Assertions.assertTrue(result, "Trainee should be activated");
    }

    @Then("the trainee should be marked as active in the database")
    public void the_trainee_should_be_marked_as_active_in_the_database() {
        Optional<TraineeDTO> result = traineeService.getTraineeByUsername(savedTrainee.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result.isPresent(), "Trainee should be found in the database");
        Assertions.assertTrue(result.get().getUser().isActive(), "Trainee should be marked as active");
    }

    @When("the trainee is deactivated")
    public void the_trainee_is_deactivated() {
        boolean result = traineeService.updateTraineeActiveStatus(savedTrainee.getUser().getUsername(), false, "txn123");
        Assertions.assertTrue(result, "Trainee should be deactivated");
    }

    @Then("the trainee should be marked as inactive in the database")
    public void the_trainee_should_be_marked_as_inactive_in_the_database() {
        Optional<TraineeDTO> result = traineeService.getTraineeByUsername(savedTrainee.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result.isPresent(), "Trainee should be found in the database");
        Assertions.assertFalse(result.get().getUser().isActive(), "Trainee should be marked as inactive");
    }

    @Given("multiple trainees exist in the database")
    public void multiple_trainees_exist_in_the_database() {
        traineeService.create("TraineeFirst9", "TraineeLast9", new Date(), "1234 Long Street Name", "txn124");
        traineeService.create("TrainerFirst9", "TrainerLast9", new Date(), "5678 Another Long Address", "txn125");
    }

    @When("all trainees are retrieved")
    public void all_trainees_are_retrieved() {
        List<Trainee> trainees = traineeService.getAllTrainees().orElseThrow();
        allTrainees = traineeConverter.convertModelListToDtoList(trainees);
    }

    @Then("the list of trainees should not be empty")
    public void the_list_of_trainees_should_not_be_empty() {
        Assertions.assertFalse(allTrainees.isEmpty(), "List of trainees should not be empty");
    }

    @When("the trainee is searched by username in service")
    public void the_trainee_is_searched_by_username_in_service() {
        foundTrainee = retryFindTraineeByUsername(savedTrainee.getUser().getUsername(), 3, 1000);
    }

    @Then("the trainee should be found")
    public void the_trainee_should_be_found() {
        Assertions.assertTrue(foundTrainee.isPresent(), "Trainee should be found");
    }

    @When("the trainee's trainers are updated with new trainers")
    public void the_trainee_s_trainers_are_updated_with_new_trainers() {
        List<TrainingType> trainingTypeList = trainingTypeService.getAllTrainingTypes();
        TrainingType trainingType = trainingTypeList.isEmpty()
                ? trainingTypeService.create(TrainingTypeName.CARDIO).orElseThrow(
                () -> new RuntimeException("Failed to create training type"))
                : trainingTypeList.get(0);

        Optional<Trainer> trainer1 = trainerService.create("TrainerFirst10", "TrainerLast10", trainingType.getId(), "trx1234");
        Optional<Trainer> trainer2 = trainerService.create("TrainerFirst11", "TrainerLast11", trainingType.getId(), "trx1235");

        List<Trainer> newTrainers = new ArrayList<>();
        newTrainers.add(trainer1.orElseThrow(() -> new RuntimeException("Trainer 1 creation failed")));
        newTrainers.add(trainer2.orElseThrow(() -> new RuntimeException("Trainer 2 creation failed")));

        List<TrainerDTO> result = traineeService.updateTraineeTrainers(savedTrainee.getUser().getUsername(), newTrainers, "txn123");
        Assertions.assertFalse(result.isEmpty(), "Trainee's trainers should be updated");
    }


    @Then("the trainee's trainers list should be updated in the database")
    public void the_trainee_s_trainers_list_should_be_updated_in_the_database() {
        Optional<TraineeDTO> result = traineeService.getTraineeByUsername(savedTrainee.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result.isPresent(), "Trainee should be found in the database");
        Assertions.assertEquals(2, result.get().getTrainers().size(), "Trainee should have 2 trainers");
    }

    @When("trainers not assigned to the trainee are searched")
    public void trainers_not_assigned_to_the_trainee_are_searched() {
        Optional<List<TrainerDTO>> unassignedTrainers = traineeService.findTrainersNotAssignedToTraineeByUsername(
                savedTrainee.getUser().getUsername(), true, "txn123"
        );

        Assertions.assertTrue(unassignedTrainers.isPresent(), "List of unassigned trainers should be present");
        Assertions.assertFalse(unassignedTrainers.get().isEmpty(), "List of unassigned trainers should not be empty");
    }


    @Then("the list of trainers not assigned to the trainee should be found")
    public void the_list_of_trainers_not_assigned_to_the_trainee_should_be_found() {
        Optional<List<TrainerDTO>> unassignedTrainers = traineeService.findTrainersNotAssignedToTraineeByUsername(
                savedTrainee.getUser().getUsername(), true, "txn123"
        );

        Assertions.assertTrue(unassignedTrainers.isPresent(), "List of unassigned trainers should be present");
        Assertions.assertFalse(unassignedTrainers.get().isEmpty(), "List of unassigned trainers should not be empty");

        boolean containsExpectedTrainer = unassignedTrainers.get().stream()
                .anyMatch(trainer -> trainer.getUser().getFirstName().equals("TrainerFirst10"));
        Assertions.assertTrue(containsExpectedTrainer, "TrainerFirst10 should be in the unassigned trainers list");
    }

    private Optional<TraineeDTO> retryFindTraineeByUsername(String username, int retries, long delayMillis) {
        for (int i = 0; i < retries; i++) {
            Optional<TraineeDTO> result = traineeService.getTraineeByUsername(username, "txn123");
            if (result.isPresent()) {
                return result;
            }
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return Optional.empty();
    }
}


