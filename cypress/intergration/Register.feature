Feature: UUi Registering and logging into a user account
  Scenario: Creating a valid user
    When I register a new user
    Then I can login

  Scenario: Appropriate validation is carried out and errors are clearly conveyed.
    Given I am on the register page
    When I enter invalid details
    Then Error messages is displayed

    Given I am on the register page
    When I enter an invalid password
    Then An appropriate error message is displayed