import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I select the deadlines tab", () => {
    cy.get('#deadline-tab').click()
})

Given("I select Create Deadline", () => {
    cy.get('#deadline-btn').click()
})

When("I enter {string} deadline name and {string} date", (deadlineName, deadlineDate) => {
    cy.get('#deadline-name').clear()
    cy.get('#deadlineNameError').should('have.text', 'Deadline Name must not be empty')
    cy.get('#deadline-name').type(deadlineName)
    cy.get('#deadlineNameError').should('have.text', '')
    cy.get('#deadlineDate').type(deadlineDate)
})

When('I select save on the deadline form',  () => {
    cy.get('#deadlineFormSubmitButton').click()
})

When("I select edit for {string} deadline", (deadline) => {
    cy.get(`[data-deadlinename="${deadline}"]`).contains(" Edit").click()
})

When("I change the name to {string}", (deadlineName) => {
    cy.get('#deadline-name').clear()
    cy.get('#deadline-name').type(deadlineName)
})

When("I select delete for {string} deadline", (deadlineName) => {
    cy.get(`[data-deadlinename="${deadlineName}"]`).contains(" Delete").click()
})

When('I select delete again on the conformation modal', ()=> {
    cy.get('#deleteDeadlineModalBtn').click()
})

Then('A new deadline is created', () => {
    cy.contains('Successfully Created Test 1')
    cy.get('#deadline-tab').click()
    cy.get('.card-title').should('contain.text', 'Test 1')
    cy.visit('/logout')
})

Then("Deadline name is updated successfully to {string}", (deadlineName) => {
    cy.contains(`Successfully Updated ${deadlineName}`)
    cy.visit('/logout')
})

Then("{string} deadline is successfully deleted", (deadlineName) => {
    cy.contains(`Successfully deleted ${deadlineName}`)
    cy.visit('/logout')
})