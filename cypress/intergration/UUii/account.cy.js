import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I navigate to the Account Page", () => {
    cy.get("#navbarDropdown").click()
    cy.get("a").contains("Account").click()
})

When("I edit my account", () => {
    cy.get('#edit-account-btn').click();
});

When("I change my firstname to {string}", (firstname) => {
    cy.get('#firstName').clear().type(firstname);
});

When("I change my lastname to {string}", (lastname) => {
    cy.get('#lastName').clear().type(lastname);
});

When("I change my pronouns to {string}", (pronouns) => {
    cy.get('#pronouns').clear().type(pronouns);
});

When("I change my nickname to {string}", (nickname) => {
    cy.get('#nickname').clear().type(nickname);
});

When("I change my email to {string}", (email) => {
    cy.get('#email').clear().type(email);
});

When("I change my bio to {string}", (bio) => {
    cy.get('#bio').clear().type(bio);
});

When("I save my account", () => {
    cy.get('#save-account-btn').click();
});

Then("I am redirected to my account page", () => {
    cy.url().should('eq', Cypress.config().baseUrl + '/account');
});

Then("My firstname is {string}", (firstname) => {
    cy.get('#firstName').should('have.value', firstname);
});

Then("My lastname is {string}", (lastname) => {
    cy.get('#lastName').should('have.value', lastname);
});

Then("My pronouns is {string}", (pronouns) => {
    cy.get('#pronouns').should('have.value', pronouns);
});

Then("My nickname is {string}", (nickname) => {
    cy.get('#nickname').should('have.value', nickname);
});

Then("My email is {string}", (email) => {
    cy.get('#email').should('have.value', email);
});

Then("My bio is {string}", (bio) => {
    cy.get('#bio').should('have.text', bio);
});