import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I select the create sprint button", () => {
    cy.get('#sprint-btn').click()
})

When("I enter a too long sprint name", () => {
    cy.get('#sprint-name').type('this is going to be more than 50 characters so an error message should be thrown')
})

When("I enter an emoji for sprint name" , () => {
    cy.get('#sprint-name').clear().type('😀')
})

When("I enter an emoji for sprint description", () => {
    cy.get('#sprint-description').clear().type('😀')
})

Then("I get error use of emoji errors for both sprint name and description", () => {
    cy.get('#sprintNameError').should('have.text', "Sprint name must not contain an emoji")
    cy.get('#descriptionError').should('have.text', "Sprint description must not contain an emoji")
})
Then("Correct sprint name is too long validation is carried out", () => {
    cy.get('#sprintNameError').should('have.text', "Sprint name must be less than 50 characters")
})