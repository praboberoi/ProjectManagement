Feature: U1 Events on the project details page
  Scenario: AC1  I can create an event on the project details page.
    Given I login as an admin
    And I select the CypressProject project
    And I select the create event button
    When I create a new event "Coding Competition"
    Then The new event "Coding Competition" is created with a success message

  Scenario: AC4: I can modify an event
    Given I login as an admin
    And I select the CypressProject project
    When I edit the event "Coding Competition"
    Then The event name is successfully updated to "Updated Coding Competition"

  Scenario: AC 6: I should be able to type the text for the event name. As I type, I should be told
  how many characters are left (i.e., how many more characters I am allowed to type).
    Given I login as an admin
    And I select the CypressProject project
    And I select the create event button
    When I type in an event name "Tech Meeting"
    Then I am told I have used "12 " characters

    Given I login as an admin
    And I select the CypressProject project
    And I select the create event button
    When I type in an event name that is very long
    Then An error message is displayed showing that the event name has exceeded the maximum characters

  Scenario: AC7: I should be able to enter dates using a calendar widget to reduce errors.
  The calendar widget should only allow valid dates that are in the project range.
    Given I login as an admin
    And I select the CypressProject project
    And I select the create event button
    When The event start date is outside the project date
    Then An error message is shown with the valid event dates

  Scenario: AC 8: I should be able to enter valid times for the event easily using
  some similar widget or method that reduces input errors and error messages.
    Given I login as an admin
    And I select the CypressProject project
    And I select the create event button
    When I enter the valid date and times for the event
    Then The event is successfully saved


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