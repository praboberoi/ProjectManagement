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
      When I type in an event name that is very long
      Then Event name validation is carried out

      Scenario: AC 7: I should be able to enter dates using a calendar widget to reduce errors.
        Given I login as an admin
        And I select the CypressProject project
        And I select the create event button
        When The start date is after the end date
        Then Date validation is carried out