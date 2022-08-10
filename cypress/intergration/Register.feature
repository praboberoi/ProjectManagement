Feature: UUi Registering and logging into a user account
  Scenario: Creating a valid user
    When I register a new user
    Then I can login

  Scenario: Appropriate validation is carried out and errors are clearly conveyed.
    When I enter an invalid email
    Then An error message is displayed