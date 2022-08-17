Feature: UUvi Project Details
  Scenario: AC 1: As a user, I can browse to a page that contains the project details.
    Given I login as an admin
    When I select the CypressProject project
    Then I am on the CypressProject project page

  Scenario: AC 1: As a user, I can browse to a page that contains the project details.
    Given I login as a student
    When I select the CypressProject project
    Then I am on the CypressProject project page