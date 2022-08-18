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