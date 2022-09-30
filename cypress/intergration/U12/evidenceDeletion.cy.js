import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I navigate to {string} page", (address) => {
    cy.contains(".nav-link", address).click()
})

Then("I Create a new Evidence with {string} as the title", (title) => {
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

Given("Evidence with {string} as the title exists", (title) => {
    cy.get('#evidence-name').should('have.text', title)
})


When("I select delete evidence on {string}", (evidenceName) => {
    cy.get('#evidence-list').contains(evidenceName).parents('.card-body').contains("Delete").click()
})


Then("A prompt appears with {string} in the prompt", (title) => {
    cy.get('#deleteEvidenceConformationModal').should('have.attr', 'aria-modal', "true")
    cy.get('#deleteMessage').should('have.text' ,`Are you sure you want to delete ${title}?`)
})

Then("I select {string} on the evidence deletion prompt", (option) => {
    cy.get('#deleteEvidenceConformationModal').find('.modal-footer').contains(option).click()
})

Then("The evidence delete prompt disappears", () => {
    cy.get('#deleteEvidenceConformationModal').should('have.class', 'show')
})

Then("Evidence with {string} as the title still exists", (title) => {
    cy.get('#evidence-name').should('have.text', title)
})

Then("Evidence with {string} as the title is deleted", (title) => {
    cy.contains('.main').should('not.exist', title)
})