Feature: Trainee Management Integration Test
  Scenario: Create a new trainee
    Given trainee calls endpoint "trainees" with the following JSON request body:
    """
    {
      "firstName": "John",
      "lastName": "Doe",
      "dateOfBirth": "1995-05-15",
      "address": "123 Main Street"
    }
    """
    When the trainee sends a POST request in order to authenticate
    Then the authentication response status code should be 201

  Scenario: Create a duplicate trainee
    Given trainee calls endpoint "trainees" with the following JSON request body:
    """
    {
      "firstName": "John",
      "lastName": "Doe",
      "dateOfBirth": "1995-05-15",
      "address": "123 Main Street"
    }
    """
    When the trainee sends a POST request in order to authenticate
    Then the authentication response status code should be 409
