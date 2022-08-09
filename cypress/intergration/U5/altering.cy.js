import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I remove users from group", () => {
    cy.intercept({
        method: 'POST',
        url: '/groups/*/removeMembers',
    }).as('deleteCheck')

    cy.get('#remove-users').click()
    cy.wait("@deleteCheck")
});

When("I drag user {word} to group {word}", (user, group) => {
    cy.intercept({
        method: 'POST',
        url: '/groups/*/addMembers',
    }).as('addCheck')

    cy.intercept({
        method: 'GET',
        url: '/groups/list',
    }).as('listCheck')

    const dataTransfer = new DataTransfer()
    cy.get('tr.user-row').contains(user).trigger('dragstart', { dataTransfer })
    cy.get('a').contains(group).trigger('drop', { dataTransfer })

    cy.wait("@addCheck")
    cy.wait("@listCheck")
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

Then("User {word} is in group", (user) => {
    cy.get('tr.user-row').contains(user).should('exist')
});

Then("Remove users not available", () => {
    cy.get('#remove-users').should('not.exist')
});