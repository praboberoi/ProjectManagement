Feature: U5. Groups and group membership
  Scenario: AC5 It is visually obvious (e.g., background shading) which students are selected.
    Given I login as an admin
    And I navigate to Groups
    When I click on user 0
    Then User 0 is highlighted

  Scenario: AC5 Ctrl/command keys work as expected when I am selecting students.
    Given I login as an admin
    And I navigate to Groups
    When I click on user 0
    And I ctrl click on user 5
    Then User 0 is highlighted
    And User 5 is highlighted

  Scenario: AC5 Shift keys work as expected when I am selecting students. It is visually obvious (e.g., background shading) which students are selected.
    Given I login as an admin
    And I navigate to Groups
    When I click on user 0
    And I shift click on user 5
    Then User 0 is highlighted
    And User 5 is highlighted
    And User 3 is highlighted

  Scenario: AC5 Ctrl/command and shift keys work as expected when I am selecting students. It is visually obvious (e.g., background shading) which students are selected.
    Given I login as an admin
    And I navigate to Groups
    When I click on user 0
    And I shift click on user 5
    And I ctrl click on user 8
    Then User 0 is highlighted
    And User 3 is highlighted
    And User 5 is highlighted
    And User 8 is highlighted