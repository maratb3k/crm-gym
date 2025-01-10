Feature: Trainer Service Component Test

  Scenario: Create a new trainer
    Given a new trainer with first name "Jane", last name "Doe", and specialization "YOGA"
    When the trainer is created
    Then the trainer should be successfully created in the database

  Scenario: Update a trainer
    Given a new trainer with first name "Venna", last name "Doe", and specialization "YOGA"
    When the trainer updates their profile with new first name "Janet" and new last name "Smith"
    Then the trainer's profile should be updated in the database

  Scenario: Delete a trainer
    Given a trainer with username "James.Bond" exists
    When the trainer is deleted
    Then the trainer should no longer exist in the database

  Scenario: Activate a trainer
    Given a trainer with username "Trainer1.Doe" exists
    When the trainer is activated
    Then the trainer should be marked as active in the database

  Scenario: Deactivate a trainer
    Given a trainer with username "Trainer2.Doe" exists
    When the trainer is deactivated
    Then the trainer should be marked as inactive in the database

  Scenario: Retrieve all trainers
    Given multiple trainers exist in the database
    When all trainers are retrieved
    Then the list of trainers should not be empty
