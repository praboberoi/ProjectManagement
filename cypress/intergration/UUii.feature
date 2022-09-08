Feature: UUii Editing a user account
  Scenario: AC 2: My account page reflects any edits I have made. AC 4: Any validation still works.
    Given I login as a student
    And I navigate to 'Account Page'
    When I edit my account
    And I change my firstname to 'CypressEditFirstname'
    And I change my lastname to 'CypressEditLastname'
    And I change my pronouns to 'Cypress/Pronoun'
    And I change my nickname to 'CypressNickname'
    And I change my email to 'CypressEdit@cypress.com'
    And I change my bio to 'Cypress Bio'
    And I save my account
    Then I am redirected to my account page
    And My firstname is 'CypressEditFirstname'
    And My lastname is 'CypressEditLastname'
    And My pronouns is 'Cypress/Pronoun'
    And My nickname is 'CypressNickname'
    And My email is 'CypressEdit@cypress.com'
    And My bio is 'Cypress Bio'