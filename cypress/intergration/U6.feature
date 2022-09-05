Feature: U6. Group settings and single repository settings
    Scenario: Misc. A git repository can be connected to with valid information
        Given I login as an admin
        And I navigate directly to "group/1"
        When I set valid repo settings
        Then A repo is connected