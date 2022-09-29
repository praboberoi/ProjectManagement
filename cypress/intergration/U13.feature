Feature: U13. Modifying pieces of evidence

  Scenario: AC2. Clicking on this icon allows me to edit this piece of evidence.
  It should be obvious that I am in edit mode. The buttons for save and cancel appear;
  the save button is initially visible but disabled. The save button is only enabled if the piece of evidence has been modified in some way.
  In the edit mode, the edit icon is not visible.
    Given I login as an admin
    And I navigate to "My Evidence" page
    And I Create a new Evidence with "Cypress Testing" as the title
    When I select Edit for "Cypress Testing" evidence
    Then Then An edit evidence modal opens up
    And "Save" and "Close" buttons are present on the evidence modal
    And "Save" button is disabled on the the evidence modal
    And "Save" button is visible when title is changed to "Cypress Testing Edit"
    And "Save" button is disabled when an emoji is added to the title
