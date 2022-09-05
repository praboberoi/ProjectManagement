Feature: UUvi Adding/deleting roles for a user
  Scenario: AC 2: Clicking on the “x” deletes that role from that user.
    Given I login as an admin
    And I navigate to 'Users'
    When I remove a Student role from user CypressGuineaPig
    Then User CypressGuineaPig isn't a Student