Feature: UUiv User's profile photo

  Scenario: AC5 - I can delete my profile photo
    Given User exists with profile photo
    When User deletes their photo
    Then Photo is deleted
    And User has no photo

  Scenario: AC5 - I can delete my profile photo with no image
    Given User exists with no profile photo
    When User deletes their photo
    Then Photo not found error is thrown
    And User has no photo
