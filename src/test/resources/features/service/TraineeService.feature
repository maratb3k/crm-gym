Feature: Trainee Service Component Test

  Scenario: Create a new trainee
    Given a new trainee with first name "John", last name "Doe", and date of birth "1990-01-01"
    When the trainee is created
    Then the trainee should be successfully created in the database

  Scenario: Update a trainee
    Given a trainee with username "John.Doe" exists
    When the trainee updates their profile with new first name "Johnny", new last name "Doyle", and address "123 Street"
    Then the trainee's profile should be updated in the database

  Scenario: Delete a trainee
    Given a trainee with username "John.Doe" exists
    When the trainee is deleted
    Then the trainee should no longer exist in the database

  Scenario: Activate a trainee
    Given a trainee with username "TraineeFirst3.TraineeLast3" exists
    When the trainee is activated
    Then the trainee should be marked as active in the database

  Scenario: Deactivate a trainee
    Given a trainee with username "TraineeFirst4.TraineeLast4" exists
    When the trainee is deactivated
    Then the trainee should be marked as inactive in the database

  Scenario: Retrieve all trainees
    Given multiple trainees exist in the database
    When all trainees are retrieved
    Then the list of trainees should not be empty

  Scenario: Find trainee by username
    Given a trainee with username "TraineeFirst5.TraineeLast5" exists
    When the trainee is searched by username in service
    Then the trainee should be found

  Scenario: Update trainers list for trainee
    Given a trainee with username "TraineeFirst6.TraineeLast6" exists
    When the trainee's trainers are updated with new trainers
    Then the trainee's trainers list should be updated in the database

  Scenario: Find trainers not assigned to trainee
    Given a trainee with username "TraineeFirst7.TraineeLast7" exists
    When trainers not assigned to the trainee are searched
    Then the list of trainers not assigned to the trainee should be found
