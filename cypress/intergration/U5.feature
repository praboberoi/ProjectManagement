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

  Scenario: AC6 I can select user(s) from a group and remove them from the group.
    Given I login as an admin
    And I navigate to Groups
    And I select group 1285322
    When I select user CypressGroupGuineaPig
    And I remove users from group
    Then User CypressGroupGuineaPig is not in group

  Scenario: AC6 Attempting to remove a user from “members without a group” does nothing.
    Given I login as an admin
    And I navigate to Groups
    When I select user CypressGroupGuineaPig
    Then Remove users not available

  Scenario: AC1 removing a user from this group removes their teacher role.
    Given I login as an admin
    And I navigate to Groups
    And I select the Teaching Staff group
    When I select user CypressGroupGuineaPig
    And I remove users from group
    Then User CypressGroupGuineaPig is not in group

  # Scenario: AC1 I cannot remove myself from the teacher group from this page unless I have a higher role (e.g., admin).

  # Scenario: AC1 Adding a user to this group automatically gives them the role “teacher”.
