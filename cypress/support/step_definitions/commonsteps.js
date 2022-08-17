import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I login as an admin", () => {
    cy.login("CypressAdmin", "CypressUser1")
});

Given("I login as a student", () => {
    cy.login("CypressStudent", "CypressUser1")
});

Given("I navigate to {word}", (location) => {
    cy.contains(".nav-link", location).click()
});

When("I select the {word} project", (project) => {
    cy.contains(project).click()
})