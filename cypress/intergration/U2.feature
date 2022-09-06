Feature: U2. Deadlines on the project details page
  Scenario: AC1 I should be able to create a deadline from the project details page.

  Given I login as an admin
  And I select the CypressProject project
  And I select the deadlines tab
  And I select Create Deadline
  When I enter "CypressTest1" deadline name and "2022-07-15T02:00" date
  And I select save on the deadline form
  Then A new deadline is created

  Scenario: AC1 I should be able to edit a deadline from the project details page.
  Given I login as an admin
  And I select the CypressProject project
  And I select the deadlines tab
  When I select edit for "CypressTest1" deadline
  And I change the name to "CypressTest2"
  And I select save on the deadline form
  Then Deadline name is updated successfully to "CypressTest2"

  Scenario: AC1 I should be able to delete a deadline from the project details page.

    Given I login as an admin
    And I select the CypressProject project
    And I select the deadlines tab
    When I select delete for "CypressTest2" deadline
    And I select delete again on the conformation modal
    Then "CypressTest2" deadline is successfully deleted

