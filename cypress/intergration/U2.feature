Feature: U2. Deadlines on the project details page
  Scenario: AC1 I should be able to create a deadline from the project details page.

    Given I login as an admin
    And I select the CypressProject project
    And I select the deadlines tab
    And I select Create Deadline
    When I enter "CypressTest1" deadline name and "2022-07-15T02:00" date
    And I select save on the deadline form
    Then A new deadline is created

    Given I login as an admin
    And I select the CypressProject project
    And I select the deadlines tab
    And I select Create Deadline
    When I enter a deadline name with an emoji
    Then An error messages is displayed for using an emoji

    Given I login as an admin
    And I select the CypressProject project
    And I select the deadlines tab
    And I select Create Deadline
    When I clear the deadline name
    Then An appropriate name error is displayed for the empty name field

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

  Scenario: AC5 I should be able to type the text for the deadline name. As I type I should be told how many characters are left.
    Given I login as an admin
    And I select the CypressProject project
    And I select the deadlines tab
    And I select Create Deadline
    When I enter "CypressTest1" deadline name and "2022-07-15T02:00" date
    Then I can see I have used "12" characters

  Scenario: AC6 The calendar widget should only allow valid dates that are in the project range.
    Given I login as an admin
    And I select the CypressProject project
    And I select the deadlines tab
    And I select Create Deadline
    When I enter an invalid deadline date
    Then I am unable to click submit

  Scenario: AC6 The calendar should default to the current date.
    Given I login as an admin
    And I select the CypressProject project
    And I select the deadlines tab
    When I select Create Deadline
    Then The current date is the deadline date