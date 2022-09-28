Feature: U3. Milestones on the project details page
  Scenario: AC1 I should be able to create a milestone from the project details page.
    Given I login as an admin
    And I select the CypressProject project
    And I select the milestones tab
    And I select Create Milestone
    When I enter "CypressTest1" milestone name and "2022-07-15" date
    And I select save on the milestone form
    Then A new milestone is created

  Scenario: AC1 I should be able to edit a milestone from the project details page.
    Given I login as an admin
    And I select the CypressProject project
    And I select the milestones tab
    When I select edit for "CypressTest1" milestone
    And I change the name to "CypressTest2"
    And I select save on the milestone form
    Then Milestone name is updated successfully to "CypressTest2"

  # Uncomment when delete functionality is added
  # Scenario: AC1 I should be able to delete a milestone from the project details page.
  #   Given I login as an admin
  #   And I select the CypressProject project
  #   And I select the milestones tab
  #   When I select delete for "CypressTest2" milestone
  #   And I select delete again on the conformation modal
  #   Then "CypressTest2" milestone is successfully deleted

  Scenario: AC5 I should be able to type the text for the milestone name. As I type I should be told how many characters are left.
    Given I login as an admin
    And I select the CypressProject project
    And I select the milestones tab
    And I select Create Milestone
    When I enter "CypressTest1" milestone name and "2022-07-15" date
    Then I can see I have used "12" characters

    Given I login as an admin
    And I select the CypressProject project
    And I select the milestones tab
    And I select Create Milestone
    When I enter a milestone name with an emoji
    Then An error messages is displayed for using an emoji

  Scenario: AC6 The calendar widget should only allow valid dates that are in the project range.
    Given I login as an admin
    And I select the CypressProject project
    And I select the milestones tab
    And I select Create Milestone
    When I enter an invalid milestone date
    Then I am unable to click submit

  Scenario: AC6 The calendar should default to the current date.
    Given I login as an admin
    And I select the CypressProject project
    And I select the milestones tab
    When I select Create Milestone
    Then The current date is the milestone date

  Scenario: AC10 My page should display any updates/changes without me having to refresh the page.
    Given I login as an admin
    And I select the CypressProject project
    And I select the milestones tab
    When I call create milestone 'Cypress auto update'
    Then 'Cypress auto update' exists