import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I remove users from group", () => {
    cy.intercept({
        method: 'POST',
        url: '/groups/*/removeMembers',
    }).as('deleteCheck')

    cy.get('#remove-users').click()
    cy.wait("@deleteCheck")
});

When("I drag user {word} to group {string}", (user, group) => {
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
    cy.intercept({
        method: 'GET',
        url: '/groups/*',
    }).as('update')

    cy.get('#group-settings-icon').click()
    cy.get('#delete-group-btn').click()
    cy.get('#deleteModal button').contains("Delete").should('be.visible').click()
    cy.get('#messageSuccess').should('contain', 'Group deleted successfully')
    
    cy.wait('@update')
})

When("I create the group {string}, {string}", (groupName, groupLongName) => {
    cy.intercept({
        method: 'GET',
        url: '/groups/*',
    }).as('update')

    cy.get('#create-btn').click()
    cy.get('#shortName').clear().type(groupName, { delay: 0 })

    cy.get('#longName').clear().type(groupLongName, { delay: 0 })
    cy.get('#save-submit-button').contains("Create").should('be.visible').click()
    cy.get('#messageSuccess').should('contain', `Group created successfully`)

    cy.wait('@update')
})

When("I try create the group {string}, {string}", (groupName, groupLongName) => {
    cy.get('#create-btn').click()
    cy.get('#shortName').clear().type(groupName, { delay: 0 })

    cy.get('#longName').clear().type(groupLongName, { delay: 0 })
    cy.get('#save-submit-button').contains("Create").click()
})

Then("Error messages are displayed for using an emojis for both short and long names", () => {
    cy.get('#shortNameError').should('have.text', "Group short name must not contain an emoji");
    cy.get('#longNameError').should('have.text', "Group long name must not contain an emoji");
})

When("I change the group name to {string}", (newName) => {
    cy.intercept({
        method: 'GET',
        url: '/groups/*',
    }).as('update')

    cy.get('#group-settings-icon').click()
    cy.get('#edit-group-btn').click()
    cy.get('#editGroupForm #shortName').clear().type(newName, { "delay": 0 })
    cy.get('#editGroupForm #shortName').should('have.value', newName)
    cy.get('#editModal button').contains("Save").should('be.visible').click()

    cy.wait('@update')
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