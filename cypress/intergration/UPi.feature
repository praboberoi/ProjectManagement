Feature: UPi Project Details
  Scenario: AC 1: As a user, I can browse to a page that contains the project details.
    Given I login as an admin
    When I select the CypressProject project
    Then I am on the CypressProject project page

  Scenario: AC 1: As a user, I can browse to a page that contains the project details.
    Given I login as a student
    When I select the CypressProject project
    Then I am on the CypressProject project page

  Scenario: AC 8: As a teacher, I can edit any of the details except for sprint labels.  All changes are persistent.
    Given I login as an admin
    And I select the CypressProject project
    And I select the create sprint button
    When I enter a too long sprint name
    Then Correct sprint name is too long validation is carried out

  Scenario: AC7: When a new sprint is first created, defaults for the mandatory fields are automatically added to help the teacher. The default sprint name is the sprint label. A default sprint start date is either the project start date or the day after the previous sprint end. A default sprint end date is 3 weeks after the default start date.
    Given I login as an admin
    When I select the CypressProject project
    And I start to create a sprint
    Then The default sprint name is the sprint label
    And The sprint start date is '2022-07-15'
    And The sprint end date is '2022-08-05'

  Scenario: AC2: As a teacher, I can create a sprint easily (e.g., a “+” button to add another).
    Given I login as an admin
    When I select the CypressProject project
    Then Create sprint button exists

  Scenario: AC2: As a teacher, I can create a sprint easily.
    Given I login as an admin
    When I select the CypressProject project
    And I create a sprint "Cypress Sprint 1"
    Then Sprint "Cypress Sprint 1" has label "Sprint 1"

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
