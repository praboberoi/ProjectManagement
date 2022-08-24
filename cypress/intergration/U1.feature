Feature: U1 Events on the project details page
  Scenario: AC 10: My page should display any updates/changes without me having to refresh the page. (delete)
    Given I login as an admin
    And I select the CypressProject project
    When I call delete event on "Cypress Websocket Delete Event"
    Then "Cypress Websocket Delete Event" doesn't exist

  Scenario: AC 10: My page should display any updates/changes without me having to refresh the page. (edit)
    Given I login as an admin
    And I select the CypressProject project
    When I call edit event on "Cypress Websocket Edit Event"
    Then "Cypress Websocket Edited Event" exists
    
  Scenario: AC 10: My page should display any updates/changes without me having to refresh the page. (create)
    Given I login as an admin
    And I select the CypressProject project
    When I call create event on "Cypress Websocket Created Event"
    Then "Cypress Websocket Created Event" exists