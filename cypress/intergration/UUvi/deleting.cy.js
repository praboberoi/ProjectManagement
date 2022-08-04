import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I remove a {word} role from user {word}", (role, user) => {
    cy.intercept({
        method: 'DELETE',
        url: '/usersList/removeRole',
      }).as('deleteCheck')
    cy.contains(user).siblings().contains("button", role).click()
    cy.wait("@deleteCheck")
    cy.get(".alert-success")
});

Then("User {word} isn't a {word}", (user, role) => {
    cy.contains(user).siblings().contains("button", role).should('not.exist')
});