Feature: U2. Deadlines on the project details page
  Scenario: AC1 I should be able to create a deadline from the project details page.

  Given I login as an admin
  And I select the CypressProject project
  And I select the deadlines tab
  And I select Create Deadline
  When I enter "Test 1" name and "2022-07-15T02:00" date
  And I select save
  Then A new deadline is created

  Scenario: AC1 I should be able to delete a deadline from the project details page.
  Given I login as an admin
  And I select the CypressProject project
  And I select the deadlines tab
  When I select edit for "Test 1" deadline
  And I change the name to "Test 2"
  And I select save
  Then Deadline name is updated successfully to "Test 2"

  Scenario: AC1 I should be able to delete a deadline from the project details page.

    Given I login as an admin
    And I select the CypressProject project
    And I select the deadlines tab
    When I select delete for "Test 2" deadline
    And I select delete again on the conformation modal
    Then "Test 2" deadline is successfully deleted

