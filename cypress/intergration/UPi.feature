Feature: UPi Project Details

  Scenario: AC 1: As a user, I can browse to a page that contains the project details.
    Given I login as an admin
    When I select the CypressProject project
    Then I am on the CypressProject project page

  Scenario: AC 1: As a user, I can browse to a page that contains the project details.
    Given I login as a student
    When I select the CypressProject project
    Then I am on the CypressProject project page

  Scenario: AC2: As a teacher, I can create and add all the details for a project. Appropriate validation is always applied. All changes are persistent.
    Given I login as an admin
    When I select Create Project
    And I enter CypressTest as a Project Name
    And I enter '2022-07-15' as the project start date
    And I enter '2023-07-15' as the project end date
    And I enter "This is a test" as the project description
    And I click on Create button
    Then A new Project with CypressTest is created

    Given I login as an admin
    When I select Create Project
    And I enter a emojis for the project name and description
    Then Error message is displayed for using emojis for both project name and description

  Scenario: AC6: As a teacher, I can create a sprint easily (e.g., a “+” button to add another).
    Given I login as an admin
    When I select the CypressProject project
    Then Create sprint button exists

  Scenario: AC7: When a new sprint is first created, defaults for the mandatory fields are automatically added to help the teacher. The default sprint name is the sprint label. A default sprint start date is either the project start date or the day after the previous sprint end. A default sprint end date is 3 weeks after the default start date.
    Given I login as an admin
    When I select the CypressProject project
    And I start to create a sprint
    Then The default sprint name is the sprint label
    And The sprint start date is '2022-07-15'
    And The sprint end date is '2022-08-04'

  Scenario: AC6: As a teacher, I can create a sprint easily.
    Given I login as an admin
    When I select the CypressProject project
    And I create a sprint "Cypress Sprint 1"
    Then Sprint "Cypress Sprint 1" has label "Sprint 1"

  Scenario: AC 8: As a teacher, I can edit any of the details except for sprint labels.  All changes are persistent.
    Given I login as an admin
    And I select the CypressProject project
    And I select the create sprint button
    When I enter an emoji for sprint name
    And I enter an emoji for sprint description
    Then I get error use of emoji errors for both sprint name and description

  Scenario: AC 8: As a teacher, I can edit any of the details except for sprint labels.  All changes are persistent.
    Given I login as an admin
    And I select the CypressProject project
    And I select the create sprint button
    When I enter a too long sprint name
    Then Correct sprint name is too long validation is carried out

 Scenario: AC 8: As a teacher, I can edit any of the details except for sprint labels.
    Given I login as an admin
    When I select the CypressProject project
    And I edit the sprint "Cypress Sprint 1"
    And I change the sprint's name to "Cypress edited sprint"
    And I change the sprint's start-date to "2022-07-16"
    And I change the sprint's description to "This is an edited sprint"
    And I save the sprint
    Then Sprint is edited successfully

  Scenario: AC 9: As a teacher, I can delete any of the sprints.
    Given I login as an admin
    When I select the CypressProject project
    And I delete the sprint "Cypress edited sprint"
    Then The sprint "Cypress edited sprint" doesn't exist

    Scenario: AC11: Any errors are shown immediately and close to the site of the error (e.g., a red box around a field with the warning message underneath it).
      Given I login as an admin
      When I select Create Project
      And I enter '2021-07-15' as the project start date
      Then I am unable to create the project
      And "Project must have started in the last year." error message is displayed under the start date

    Scenario: AC11: Any errors are shown immediately and close to the site of the error (e.g., a red box around a field with the warning message underneath it).
      Given I login as an admin
      When I select Create Project
      And I enter '2021-07-15' as the project end date
      Then I am unable to create the project
      And "Start date must be before the end date." error message is displayed under the start date
      And "End date must be after the start date" error message is displayed under the end date