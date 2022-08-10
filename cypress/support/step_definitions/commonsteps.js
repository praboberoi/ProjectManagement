import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I login as an admin", () => {
    cy.login("CypressAdmin", "CypressUser1")
});

Given("I navigate to {word}", (location) => {
    cy.contains(".nav-link", location).click()
});