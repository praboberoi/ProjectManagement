import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";


Given("I navigate to {string} page", (address) => {
    cy.contains(".nav-link", address).click()
})

Given("I Create a new Evidence with {string} as the title", (title) => {
    cy.get("#create-evidence-btn").click()
    cy.get("#evidence-title").clear().type(title, {delay:0})
    cy.get("#evidence-description").clear().type("This is a test",{delay:0})

    cy.intercept({
        method: 'GET',
        url: '/user/*/evidence/**',
    }).as('update')
    cy.get("#evidenceFormSubmitButton").click()

    cy.wait('@update')
})

When("I select Edit for {string} evidence", (title) => {
    cy.intercept({
        method: 'GET',
        url: '/evidence/*/editEvidence',
    }).as('editEvidenceCheck')
    cy.get(`[data-title="${title}"]`).contains("Edit").click()
})

Then("Then An edit evidence modal opens up", () => {
    cy.get('#evidenceFormModal').should('have.attr', 'aria-modal', "true")
})

Then("{string} and {string} buttons are present on the evidence modal", (save, close) => {
    cy.get('#evidenceFormSubmitLabel').should('have.text', save)
    cy.get('#evidenceFormSubmitButton').siblings().should('contain.text', close)
})

Then("{string} button is disabled on the the evidence modal", (save) => {
    cy.get('#evidenceFormSubmitButton').should('have.attr', "disabled")
})

Then("{string} button is visible when title is changed to {string}", (save, title) => {
    cy.get('#evidence-title').clear().type(title,{delay:0})
    cy.get('#evidenceFormSubmitButton').should('not.have.attr', "disabled")

})

Then("{string} button is disabled when an emoji is added to the title", (save) => {
    cy.get("#evidence-title").type('😀', {delay:0})
    cy.get('#evidenceFormSubmitButton').should('have.attr', "disabled")
})
