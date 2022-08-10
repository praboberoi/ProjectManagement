import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";
When("I register a new user", () => {
    cy.visit("")
    cy.get("#register").click()
    cy.get('#username').type('CypressRegister')
    cy.get('#firstName').type('Cypress')
    cy.get('#lastName').type('Register')
    cy.get('#nickname').type('Cypress')
    cy.get('#email').type('automated@cypress.com')
    cy.get('#password').type('CypressUser1')
    cy.get('#confirmPassword').type('CypressUser1')
    cy.get("#submit").click()    
});

Then("I am logged in", () => {
    cy.url().should('eq', Cypress.config().baseUrl + '/dashboard')
});
Then("I can login", () => {
    cy.login("CypressRegister", "CypressUser1")
    cy.url().should('eq', Cypress.config().baseUrl + '/dashboard')
});

Given("I am on the register page", () => {
    cy.visit("")
    cy.get("#register").click()
});

When("I enter invalid details", () => {
    cy.get('#username').type('A')
    cy.get('#firstName').type('B')
    cy.get('#lastName').type('C')
    cy.get('#email').type('automated@')
    cy.get('#password').type('CypressUser1')
    cy.get('#confirmPassword').type('CypressUser')
    cy.get("#submit").click()

});

Then("Error messages is displayed", () => {
    cy.get("#usernameError").should('have.text', "Username must be between 3 and 16 characters with no special characters.")
    cy.get("#firstNameError").should('have.text', "First name must be between 2 and 32 characters with no special characters or digits.")
    cy.get("#lastNameError").should('have.text', "Last name must be between 2 and 32 characters with no special characters or digits.")
    cy.get("#emailError").should('have.text', "Email must be in the form username@domainName.domain.")
    cy.get("#confirmPasswordError").should('have.text', "Passwords do not match.")
});

When("I enter an invalid password", () => {
    cy.get('#password').type('a')
    cy.get("#submit").click()

});

Then("An appropriate error message is displayed", () => {
    cy.get("#passwordError").should('have.text', "Password must contain 8-16 characters, a digit and uppercase character.")
});