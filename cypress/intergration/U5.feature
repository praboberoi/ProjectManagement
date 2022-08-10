Feature: U5. Groups and group membership
  # Scenario: AC5 It is visually obvious (e.g., background shading) which students are selected.
  #   Given I login as an admin
  #   And I navigate to Groups
  #   When I click on user 0
  #   Then User 0 is highlighted

  # Scenario: AC5 Ctrl/command keys work as expected when I am selecting students.
  #   Given I login as an admin
  #   And I navigate to Groups
  #   When I click on user 0
  #   And I ctrl click on user 5
  #   Then User 0 is highlighted
  #   And User 5 is highlighted

  # Scenario: AC5 Shift keys work as expected when I am selecting students. It is visually obvious (e.g., background shading) which students are selected.
  #   Given I login as an admin
  #   And I navigate to Groups
  #   When I click on user 0
  #   And I shift click on user 5
  #   Then User 0 is highlighted
  #   And User 5 is highlighted
  #   And User 3 is highlighted

  # Scenario: AC5 Ctrl/command and shift keys work as expected when I am selecting students. It is visually obvious (e.g., background shading) which students are selected.
  #   Given I login as an admin
  #   And I navigate to Groups
  #   When I click on user 0
  #   And I shift click on user 5
  #   And I ctrl click on user 8
  #   Then User 0 is highlighted
  #   And User 3 is highlighted
  #   And User 5 is highlighted
  #   And User 8 is highlighted

  # Scenario: AC6 I can select user(s) from a group and remove them from the group.
  #   Given I login as an admin
  #   And I navigate to Groups
  #   And I select group Cypress1
  #   When I select user CypressGroupGuineaPig
  #   And I remove users from group
  #   Then User CypressGroupGuineaPig is not in group

  # Scenario: AC6 Attempting to remove a user from “members without a group” does nothing.
  #   Given I login as an admin
  #   And I navigate to Groups
  #   When I select user CypressUnassignedGuineaPig
  #   Then Remove users not available

  # Scenario: AC1 removing a user from this group removes their teacher role.
  #   Given I login as an admin
  #   And I navigate to Groups
  #   And I select the Teaching Staff group
  #   When I select user CypressGroupGuineaPig
  #   And I remove users from group
  #   Then User CypressGroupGuineaPig is not in group

  # Scenario: AC4 I can select ... copy them to another group. If the source group is “members without a group”, then the user is moved to the other group
  #   Given I login as an admin
  #   And I navigate to Groups
  #   When I drag user CypressGroupGuineaPig to group Cypress2
  #   Then User CypressGroupGuineaPig is not in group
  #   And User CypressGroupGuineaPig is in group Cypress2

  # Scenario: AC4 As a teacher, I can select one or more users from any groups and copy them to another group.
  #   Given I login as an admin
  #   And I navigate to Groups
  #   And I select group Cypress2
  #   When I drag user CypressGroupGuineaPig to group Cypress1
  #   Then User CypressGroupGuineaPig is in group
  #   And User CypressGroupGuineaPig is in group Cypress1

  # Scenario: AC8 I can delete groups. I am asked to confirm that I want to remove the x group members (users will not be deleted) and delete the group and that deleting a group cannot be undone. On confirmation, the group is deleted permanently, and group members removed.
  #   Given I login as an admin
  #   And I navigate to Groups
  #   And I select group Cypress2
  #   When I delete the group
  #   Then Group Cypress2 doesn't exist

  Scenario: Editing a group
    Given I login as an admin
    And I navigate to Groups
    And I select group Cypress1
    When I change the group name to Cypress3
    Then Group Cypress1 doesn't exist
    And Group Cypress3 exists