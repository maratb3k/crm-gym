package com.example.crm_gym.steps.services;

import com.example.crm_gym.dto.TrainerDTO;
import com.example.crm_gym.dtoConverter.TrainerConverter;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.TrainingType;
import com.example.crm_gym.models.TrainingTypeName;
import com.example.crm_gym.services.TrainerService;
import com.example.crm_gym.services.TrainingTypeService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class TrainerServiceSteps {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainingTypeService trainingTypeService;

    @Autowired
    private TrainerConverter trainerConverter;

    private TrainerDTO savedTrainer;
    private Optional<TrainerDTO> foundTrainer;
    private List<TrainerDTO> allTrainers;

    @Given("a new trainer with first name {string}, last name {string}, and specialization {string}")
    public void a_new_trainer_with_first_name_last_name_and_specialization(String firstName, String lastName, String specialization) {
        List<TrainingType> trainingTypeList = trainingTypeService.getAllTrainingTypes();
        TrainingType trainingType = trainingTypeList.isEmpty()
                ? trainingTypeService.create(TrainingTypeName.YOGA).orElseThrow(
                () -> new RuntimeException("Failed to create training type"))
                : trainingTypeList.get(0);

        Trainer trainer = trainerService.create(firstName, lastName, trainingType.getId(), "txn123")
                .orElseThrow(() -> new RuntimeException("Trainer creation failed"));
        savedTrainer = trainerConverter.convertToDto(trainer);
        Assertions.assertNotNull(savedTrainer, "Saved trainer should not be null");
    }


    @When("the trainer is created")
    public void the_trainer_is_created() {
        Assertions.assertNotNull(savedTrainer, "Trainer should be created");
    }

    @Then("the trainer should be successfully created in the database")
    public void the_trainer_should_be_successfully_created_in_the_database() {
        Optional<TrainerDTO> result = trainerService.getTrainerByUsername(savedTrainer.getUser().getUsername(), "trx123");
        Assertions.assertTrue(result.isPresent(), "Trainer should be found in the database");
    }

    @Given("a trainer with username {string} exists")
    public void a_trainer_with_username_exists(String username) {
        String[] parts = username.split("\\.");

        List<TrainingType> trainingTypeList = trainingTypeService.getAllTrainingTypes();
        TrainingType trainingType = trainingTypeList.isEmpty()
                ? trainingTypeService.create(TrainingTypeName.YOGA).orElseThrow(
                () -> new RuntimeException("Failed to create training type"))
                : trainingTypeList.get(0);

        Trainer trainer = trainerService.create(parts[0], parts[1], trainingType.getId(), "txn123")
                .orElseThrow(() -> new RuntimeException("Trainer creation failed"));
        savedTrainer = trainerConverter.convertToDto(trainer);

        Assertions.assertNotNull(savedTrainer, "Saved trainer should not be null");
    }


    @When("the trainer updates their profile with new first name {string} and new last name {string}")
    public void the_trainer_updates_their_profile_with_new_first_name_and_new_last_name(String firstName, String lastName) {
        Optional<TrainerDTO> optionalTrainerDTO = trainerService.getTrainerByUsername(savedTrainer.getUser().getUsername(), "txn123");
        Assertions.assertTrue(optionalTrainerDTO.isPresent(), "Trainer should be found before updating");

        Trainer entityTrainer = trainerConverter.convertToEntity(optionalTrainerDTO.get());
        Assertions.assertNotNull(entityTrainer, "Entity trainer should not be null");

        entityTrainer.getUser().setFirstName(firstName);
        entityTrainer.getUser().setLastName(lastName);

        trainerService.update(entityTrainer, "txn123");
    }

    @Then("the trainer's profile should be updated in the database")
    public void the_trainer_s_profile_should_be_updated_in_the_database() {
        Optional<TrainerDTO> result = trainerService.getTrainerByUsername(savedTrainer.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result.isPresent(), "Trainer should be found in the database");
        Assertions.assertEquals("Janet", result.get().getUser().getFirstName(), "First name should be updated");
        Assertions.assertEquals("Smith", result.get().getUser().getLastName(), "Last name should be updated");
    }


    @When("the trainer is deleted")
    public void the_trainer_is_deleted() {
        boolean result = trainerService.deleteTrainerByUsername(savedTrainer.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result, "Trainer should be deleted");
    }

    @Then("the trainer should no longer exist in the database")
    public void the_trainer_should_no_longer_exist_in_the_database() {
        Optional<TrainerDTO> result = trainerService.getTrainerByUsername(savedTrainer.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result.isEmpty(), "Trainer should no longer exist in the database");
    }

    @Then("the trainer with specific data should be successfully created in the database")
    public void the_trainer_should_be_successfully_created_in_the_database_trainer() {
        Optional<TrainerDTO> result = trainerService.getTrainerByUsername(savedTrainer.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result.isPresent(), "Trainer should be found in the database");
    }


    @When("the trainer is activated")
    public void the_trainer_is_activated() {
        boolean result = trainerService.updateTrainerActiveStatus(savedTrainer.getUser().getUsername(), true, "txn123");
        Assertions.assertTrue(result, "Trainer should be activated");
    }

    @Then("the trainer should be marked as active in the database")
    public void the_trainer_should_be_marked_as_active_in_the_database() {
        Optional<TrainerDTO> result = trainerService.getTrainerByUsername(savedTrainer.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result.isPresent(), "Trainer should be found in the database");
        Assertions.assertTrue(result.get().getUser().isActive(), "Trainer should be marked as active");
    }

    @When("the trainer is deactivated")
    public void the_trainer_is_deactivated() {
        boolean result = trainerService.updateTrainerActiveStatus(savedTrainer.getUser().getUsername(), false, "txn123");
        Assertions.assertTrue(result, "Trainer should be deactivated");
    }

    @Then("the trainer should be marked as inactive in the database")
    public void the_trainer_should_be_marked_as_inactive_in_the_database() {
        Optional<TrainerDTO> result = trainerService.getTrainerByUsername(savedTrainer.getUser().getUsername(), "txn123");
        Assertions.assertTrue(result.isPresent(), "Trainer should be found in the database");
        Assertions.assertFalse(result.get().getUser().isActive(), "Trainer should be marked as inactive");
    }

    @Given("multiple trainers exist in the database")
    public void multiple_trainers_exist_in_the_database() {
        for (int i = 12; i <= 16; i++) {
            String firstName = "Trainer" + i;
            String lastName = "LastName" + i;
            trainerService.create(firstName, lastName, 1L, "txn" + i).orElseThrow(() -> new RuntimeException("Failed to create trainer " + firstName));
        }
    }

    @When("all trainers are retrieved")
    public void all_trainers_are_retrieved() {
        List<Trainer> trainers = trainerService.getAllTrainers().orElseThrow();
        allTrainers = trainerConverter.convertModelListToDtoList(trainers);
    }

    @Then("the list of trainers should not be empty")
    public void the_list_of_trainers_should_not_be_empty() {
        Assertions.assertFalse(allTrainers.isEmpty(), "List of trainers should not be empty");
    }
}
