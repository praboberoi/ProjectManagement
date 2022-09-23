import {Given, Then, When} from "@badeball/cypress-cucumber-preprocessor";

Given("I click create evidence", () => {
    cy.get('#create-evidence-btn').click()
});

When("I enter invalid evidence details", () => {
    cy.get('#evidence-title').clear()
    cy.get('#evidence-description').clear()
    cy.get('#evidence-description').type('a')
    cy.get('#evidence-date').clear()
});

Then("Correct evidence validation carried out", () => {
    cy.get("#evidenceDescriptionError").should('have.text', "Evidence description must be at least 2 characters")
    cy.get("#evidenceTitleError").should('have.text', "Evidence title must be at least 2 characters")
    cy.get("#evidenceDateError").should('have.text', "The evidence date must be within the selected project range")
    cy.get('#evidence-title').clear()
    cy.get('#evidence-title').type('78324(*&')
    cy.get("#evidenceTitleError").should('have.text', "Evidence title must contain some letters")

});
