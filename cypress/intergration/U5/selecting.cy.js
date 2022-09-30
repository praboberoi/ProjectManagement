import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

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

When("I ctrl + shift click on user {int}", (userIndex) => {
    cy.get('tr.user-row').eq(userIndex).click({shiftKey: true, ctrlKey: true})
});

Then("User {int} is highlighted", (userIndex) => {
    cy.get('tr.user-row').eq(userIndex).should('have.class', 'table-info')
});

Then("User {word} is in group {string}", (user, group) => {
    cy.intercept({
        method: 'GET',
        url: '/groups/*',
    }).as('groupCheck')

    cy.get('.project-card a').contains(group).click()
    cy.wait('@groupCheck')
    cy.get('tr.user-row').contains(user).should('exist')
});

Then("Group {string} doesn't exist", (group) => {
    cy.get('.project-card a').contains(group).should('not.exist')
});

Then("Group {string} exists", (group) => {
    cy.get('.project-card a').contains(group).should('exist')
});