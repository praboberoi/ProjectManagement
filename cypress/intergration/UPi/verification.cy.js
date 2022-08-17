import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Then("I am on the {word} project page", (project) => {
    cy.get("#name").contains(project)
})