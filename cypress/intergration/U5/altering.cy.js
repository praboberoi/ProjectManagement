import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I remove users from group", () => {
    cy.intercept({
        method: 'POST',
        url: '/groups/*/removeMembers',
    }).as('deleteCheck')

    cy.get('#remove-users').click()
    cy.wait("@deleteCheck")
});

When("I delete the group", () => {
    cy.get('#edit-group-tag').click()
    cy.get('#delete-group-btn').click()
    cy.get('#deleteModal').contains("Delete").click()
    cy.get('#messageSuccess').should('contain', 'deleted successfully')
})

Then("User {word} is not in group", (user) => {
    cy.get('tr.user-row').contains(user).should('not.exist')
});

Then("Remove users not available", () => {
    cy.get('#remove-users').should('not.exist')
});