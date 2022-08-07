import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I select group {int}", (groupId) => {
    cy.get('#group' + groupId + 'Card').find('a').click()
});

Given("I select the Teaching Staff group", () => {
    cy.get('#select-teachers').click()
});

Given("I select the Unassigned Members group", () => {
    cy.get('#select-unassigned').click()
});


When("I select user {word}", (user) => {
    cy.get('tr.user-row').contains(user).click()
});

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