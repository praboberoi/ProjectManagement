Feature: U6. Group settings and single repository settings
    Scenario: Misc. A git repository can be connected to with valid information
        Given I login as an admin
        And I navigate directly to "group/1"
        When I set valid repo settings
        Then A repo is connected

    Scenario: AC5. The page contains a section on the group’s code repository settings. This section is not viewable by students not in this group.
        Given I login as a student
        And I navigate directly to "group/1"
        Then I cannot see repo settings