import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I remove users from group", () => {
    cy.intercept({
        method: 'POST',
        url: '/groups/*/removeMembers',
    }).as('deleteCheck')

    cy.get('#remove-users').click()
    cy.wait("@deleteCheck")
});

Then("User {word} is not in group", (user) => {
    cy.get('tr.user-row').contains(user).should('not.exist')
});

Then("Remove users not available", () => {
    cy.get('#remove-users').should('not.exist')
});