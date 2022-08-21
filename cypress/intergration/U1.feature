Feature: U1 Creating Events
  Scenario: AC1  I can create/add an event on the project details page.
    Given I login as an admin
    And I select the CypressProject project
    When I create a new event
    Then A new event is created