import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I select the create sprint button", () => {
    cy.get('#sprint-btn').click()
})

When("I enter a too long sprint name", () => {
    cy.get('#sprint-name').type('this is going to be more than 50 characters so an error message should be thrown')
})

Then("Correct sprint name is too long validation is carried out", () => {
    cy.get('#sprintNameError').should('have.text', "Sprint Name must be less than 50 characters")
})