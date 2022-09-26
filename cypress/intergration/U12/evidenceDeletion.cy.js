import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I navigate to {string} page", (address) => {
    cy.contains(".nav-link", address).click()
})

Given("I Create a new Evidence with {string} as the title", (title) => {
    cy.get("#create-evidence-btn").click()
    cy.get("#evidence-title").clear().type(title, {delay:0})
    cy.get("#evidence-description").clear().type("This is a test",{delay:0})
    cy.get("#evidenceFormSubmitButton").click()
})

Given("Evidence with {string} as the title exists", (title) => {
    cy.get('.project-name').should('have.text', title)
})


When("I select delete evidence", () => {
    cy.get('[data-title="Cypress Testing"]').contains("Delete").click()
})


Then("A prompt appears with {string} in the prompt", (title) => {
    cy.get('#deleteEvidenceConformationModal').should('have.attr', 'aria-modal', "true")
    cy.get('#deleteMessage').should('have.text' ,`Are you sure you want to delete ${title}?`)
})

Then("I select {string} on the evidence deletion prompt", (option) => {
    cy.get('#deleteEvidenceForm').contains(option).click()
})

Then("The evidence delete prompt disappears", () => {
    cy.get('#deleteEvidenceConformationModal').should('have.attr', 'aria-hidden', "true")
})

Then("Evidence with {string} as the title still exists", (title) => {
    cy.get('.project-name').should('have.text', title)
})

Then("Evidence with {string} as the title is deleted", (title) => {
    cy.contains('.main').should('not.exist', title)
})