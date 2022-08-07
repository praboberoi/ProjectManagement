import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I click on user {int}", (userIndex) => {
    cy.get('tr.user-row').eq(userIndex).click()
});

When("I ctrl click on user {int}", (userIndex) => {
    cy.get('tr.user-row').eq(userIndex).click({ctrlKey: true})
});

When("I shift click on user {int}", (userIndex) => {
    cy.get('tr.user-row').eq(userIndex).click({shiftKey: true})
});

Then("User {int} is highlighted", (userIndex) => {
    cy.get('tr.user-row').eq(userIndex).should('have.class', 'table-info')
});