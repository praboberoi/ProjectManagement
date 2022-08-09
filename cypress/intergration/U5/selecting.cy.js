import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I select group {int}", (groupId) => {
    cy.intercept({
        method: 'GET',
        url: '/groups/*',
    }).as('groupCheck')
    cy.get('#group' + groupId + 'Card').find('a').click()
    cy.wait('@groupCheck')
});

Given("I select the Teaching Staff group", () => {
    cy.intercept({
        method: 'GET',
        url: '/groups/*',
    }).as('groupCheck')
    cy.get('#select-teachers').click()    
    cy.wait('@groupCheck')
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

Then("Group {word} doesn't exist", (group) => {
    cy.get('.project-card a').contains(group).should('not.exist')
});

