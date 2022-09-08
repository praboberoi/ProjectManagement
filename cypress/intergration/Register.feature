Feature: UUi Registering and logging into a user account
  Scenario: Creating a valid user
    When I register a new user
    Then I am logged in

  Scenario: Appropriate validation is carried out and errors are clearly conveyed.
    Given I am on the register page
    When I enter invalid details
    Then Register error messages is displayed

    Given I am on the register page
    When I enter an invalid password
    Then An appropriate password error message is displayed

    Given I am on the register page
    When I enter invalid pronouns
    Then An appropriate pronouns error message is displayed