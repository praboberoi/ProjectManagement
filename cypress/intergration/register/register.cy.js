import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";
When("I register a new user", () => {
    cy.visit("")
    cy.get("#register").click()
    cy.get('#username').type('CypressRegister')
    cy.get('#firstName').type('Cypress')
    cy.get('#lastName').type('Register')
    cy.get('#nickname').type('Cypress')
    cy.get('#pronouns').type('she/her')
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

Then("Register error messages is displayed", () => {
    cy.get("#usernameError").should('have.text', "Username must be between 3 and 16 characters with no space or special characters.")
    cy.get("#firstNameError").should('have.text', "First name must be between 2 and 32 characters with no special characters or digits.")
    cy.get("#lastNameError").should('have.text', "Last name must be between 2 and 32 characters with no special characters or digits.")
    cy.get("#emailError").should('have.text', "Email must be in the form username@domainName.domain.")
    cy.get("#confirmPasswordError").should('have.text', "Passwords do not match.")
});

When("I enter an invalid password", () => {
    cy.get('#password').type('a')
    cy.get("#submit").click()

});

When("I enter invalid pronouns", () => {
    cy.get('#pronouns').type('she-her')
    cy.get("#submit").click()
});

Then("An appropriate password error message is displayed", () => {
    cy.get("#passwordError").should('have.text', "Password must contain 8-16 characters, a digit and uppercase character.")
});

When("I register a new user with accents in username, firstname, lastname, and nickname", () => {
    cy.visit("")
    cy.get("#register").click()
    cy.get('#username').type('CypressAççěnt')
    cy.get('#firstName').type('Çyprœss')
    cy.get('#lastName').type('Rěgìstèr')
    cy.get('#nickname').type('Aççěnt')
    cy.get('#email').type('automated@cypress.com')
    cy.get('#password').type('CypressUser1')
    cy.get('#confirmPassword').type('CypressUser1')
    cy.get("#submit").click()
});

Then("An appropriate pronouns error message is displayed", () => {
    cy.get("#personalPronounsError").should('have.text', "Personal pronouns can only contain special character / and no digits.")
});