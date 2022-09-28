Feature: U12. Deleting pieces of evidence
  Scenario: AC2. When I click this icon, a prompt appears asking if I want to delete this evidence from my portfolio.
  The title of the piece of evidence should also appear on the prompt.
    Given I login as an admin
    And I navigate to "My Evidence" page
    And I Create a new Evidence with "Cypress Testing" as the title
    When I select delete evidence
    Then A prompt appears with "Cypress Testing" in the prompt

  Scenario: AC3. I can either accept the prompt or cancel it. Accepting it deletes that piece of evidence. (cancel)
    Given I login as an admin
    And I navigate to "My Evidence" page
    And Evidence with "Cypress Testing" as the title exists
    When I select delete evidence
    And A prompt appears with "Cypress Testing" in the prompt
    Then I select "Close" on the evidence deletion prompt
    And Evidence with "Cypress Testing" as the title still exists

  Scenario: AC3. I can either accept the prompt or cancel it. Accepting it deletes that piece of evidence. (delete)
    Given I login as an admin
    And I navigate to "My Evidence" page
    And Evidence with "Cypress Testing" as the title exists
    When I select delete evidence
    And A prompt appears with "Cypress Testing" in the prompt
    Then I select "Delete" on the evidence deletion prompt
    And The evidence delete prompt disappears
    And Evidence with "Cypress Testing" as the title is deleted