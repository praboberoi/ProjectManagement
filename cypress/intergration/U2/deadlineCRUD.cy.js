import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I select the deadlines tab", () => {
    cy.get('#deadline-tab').click()
})

Given("I select Create Deadline", () => {
    cy.get('#deadline-btn').click()
})

When("I enter {string} deadline name and {string} date", (deadlineName, deadlineDate) => {
    cy.get('#deadline-name').clear().should('have.value', '')
    cy.get('#deadlineNameError').should('have.text', 'Deadline Name must not be empty')
    cy.get('#deadline-name').type(deadlineName,{ delay: 0 }).should('have.value', deadlineName)
    cy.get('#deadlineNameError').should('have.text', '')
    cy.get('#deadlineDate').type(deadlineDate).should('have.value', deadlineDate)
})

When('I select save on the deadline form',  () => {
    cy.get('#deadlineFormSubmitButton').click()
})

When("I select edit for {string} deadline", (deadline) => {
    cy.contains(deadline).parents('.deadline-card').contains("Edit").click()
})

When("I change the name to {string}", (deadlineName) => {
    cy.get('#deadline-name').clear().should('have.value','')
    cy.get('#deadline-name').type(deadlineName,{ delay: 0 }).should('have.value', deadlineName)
})

When("I select delete for {string} deadline", (deadline) => {
    cy.contains(deadline).parents('.deadline-card').contains("Delete").click()
})

When('I select delete again on the conformation modal', ()=> {
    cy.get('#deleteDeadlineModalBtn').click()
})

When("I enter an invalid deadline date", () => {
    cy.get('#deadlineDate').clear().type("2021-06-10T08:30")
    cy.get('#deadlineFormSubmitButton').click()
})

Then('A new deadline is created', () => {
    cy.contains("#messageSuccess",'Successfully Created CypressTest1')
    cy.get('#deadline-tab').click()
    cy.get('.card-title').should('contain.text', 'CypressTest1')
})

Then("Deadline name is updated successfully to {string}", (deadlineName) => {
    cy.contains("#messageSuccess",`Successfully Updated ${deadlineName}`)
})

Then("{string} deadline is successfully deleted", (deadlineName) => {
    cy.contains("#messageSuccess",`Successfully deleted ${deadlineName}`)
})

Then("I can see I have used {string} characters", (charLeft) => {
    cy.contains('#deadlineCharCount',`${charLeft} `)
})

Then("An error message is displayed for the date being outside the project range", () => {
    cy.get('#deadlineDateError').should('have.text', "Deadline must start on or after 15 Jul 2022")
})

Then("The current date is the deadline date", () => {
    cy.get('#deadlineDate').should("have.value",
        new Date().toLocaleDateString().split('/').reverse().join('-') + 'T00:00')
})