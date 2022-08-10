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

    cy.url().should('eq', Cypress.config().baseUrl + '/login')
    
});

Then("I can login", () => {
    cy.login("CypressRegister", "CypressUser1")
    cy.url().should('eq', Cypress.config().baseUrl + '/dashboard')
});

When("I enter an invalid email", () => {
    cy.visit("")
    cy.get("#register").click()
    cy.get('#username').type('CypressRegister')
    cy.get('#firstName').type('Cypress')
    cy.get('#lastName').type('Register')
    cy.get('#nickname').type('Cypress')
    cy.get('#email').type('automated@')
    cy.get('#password').type('CypressUser1')
    cy.get('#confirmPassword').type('CypressUser1')
    cy.get("#submit").click()
});

Then("I can login", () => {
    cy.login("CypressRegister", "CypressUser1")
    cy.url().should('eq', Cypress.config().baseUrl + '/dashboard')
});