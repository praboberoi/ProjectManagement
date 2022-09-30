Feature: U6. Group settings and single repository settings
    Scenario: Misc. A git repository can be connected to with valid information
        Given I login as an admin
        And I navigate directly to "group/1285322"
        When I set valid repo settings
        Then A repo is connected

    Scenario: AC5. The page contains a section on the group’s code repository settings. This section is not viewable by students not in this group.
        Given I login as a student
        And I navigate directly to "group/1285322"
        Then I cannot see repo settings

    Scenario: AC5. The page contains a section on the group’s code repository settings. This section is not viewable by students not in this group. (As teacher)
        Given I login as an admin
        And I navigate directly to "group/1285322"
        Then I can see repo settings

    Scenario: AC9 If the settings are incorrect (i.e., format of settings, blank settings), an appropriate error message is shown, and the settings are not saved.
        Given I login as an admin
        And I navigate directly to "group/1285322"
        When I set invalid repo settings
        Then Repo error messages are shown

    Scenario: Misc. The user is able to toggle the recent actions tab.
        Given I login as an admin
        And I navigate directly to "group/1285322"
        When I toggle the recent actions component
        Then The recent actions component is not visible    

    Scenario: Misc. The user is able to toggle the recent actions tab.
        Given I login as an admin
        And I navigate directly to "group/1285322"
        When I toggle the recent actions component
        And I toggle the recent actions component
        Then The recent actions component is visible

    Scenario: AC3. Group members can edit (update) and save the long name; the modification is populated across the application
        Given I login as an admin
        And I navigate to 'Groups'
        When I call create group 'Cypress ws'
        Then "Cypress ws" exists

    Scenario: AC3. Group members can edit (update) and save the long name; the modification is populated across the application
        Given I login as an admin
        And I navigate to 'Groups'
        When I call edit group 'Cypress ws' to 'Cypress wse'
        Then "Cypress wse" exists

    Scenario: AC3. Group members can edit (update title) and save the long name; the modification is populated across the application
        Given I login as an admin
        And I navigate to 'Groups'
        And I navigate to group 'Cypress title update'
        When I call edit current group to 'Cypress title updated'
        Then "Cypress title updated" exists

    Scenario: AC3. Group members can edit (update) and save the long name; the modification is populated across the application
        Given I login as an admin
        And I navigate to 'Groups'
        When I call delete group 'Cypress wse'
        Then "Cypress wse" doesn't exist
