Feature: U1 Creating Events
  Scenario: AC1  I can create an event on the project details page.
    Given I login as an admin
    And I select the CypressProject project
    And I select the create event button
    When I create a new event
    Then A new event is created with a success message

  Scenario: AC 6: I should be able to type the text for the event name. As I type, I should be told
  how many characters are left (i.e., how many more characters I am allowed to type).
    Given I login as an admin
    And I select the CypressProject project
    And I select the create event button
    When I type in an event name
    Then I am told how many characters I have used

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


