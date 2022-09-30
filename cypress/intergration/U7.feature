Feature: U7. Evidence page
  Scenario: AC5 Some basic/logical validation is carried out on all fields.
    Given I login as an admin
    And I navigate to 'My Evidence'
    When I click create evidence
    And I enter invalid evidence details
    Then Correct evidence validation carried out
    And Correct evidence title character type validation carried out