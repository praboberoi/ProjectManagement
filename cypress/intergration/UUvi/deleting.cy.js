import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I remove a {word} role from user {word}", (role, user) => {
    cy.intercept({
        method: 'DELETE',
        url: '/usersList/removeRole',
    }).as('deleteCheck')

    cy.intercept({
        method: 'GET',
        url: '/usersList?*',
    }).as('nextPage')

    let checkForUser = () => {
        cy.get("#userListDataTable").then($dataTable => {
            if ($dataTable.find(':contains(' + user + ')').length == 0) {
                cy.get("#next-page").click()
                cy.wait("@nextPage")
                checkForUser()
            } else {
                cy.contains(user).siblings().contains("button", role).click()
                cy.wait("@deleteCheck")
                cy.get(".alert-success")
            }
        })
    }

    checkForUser()

});

Then("User {word} isn't a {word}", (user, role) => {
    cy.contains(user).siblings().contains("button", role).should('not.exist')
});