import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I set valid repo settings", () => {
    cy.intercept("https://eng-git.canterbury.ac.nz/**", {
        statusCode: 200,
        body: {
            id: 1,
            name: "Cypress git repo",
        },
    })
    cy.get("#repo-settings-tab").click()
    cy.get("#git-project-alias").clear().type("Cypress git project")
    cy.get("#git-project-id").clear().type("13964")
    cy.get("#git-access-token").clear().type("sVMvHmHxhJeqdZBBchDB")
    cy.get("#git-host-address").clear().type("https://eng-git.canterbury.ac.nz/")
    cy.get("#submit-repo").click()
});

When("I toggle the recent actions component", () => {
    cy.get('#actions-toggle-tab').click()
});

Then("A repo is connected", () => {
    cy.contains("Connected to repo:").should('exist')
});

Then("I cannot see repo settings", () => {
    cy.get("#repo-settings-tab").should('not.exist')
})

Then("I can see repo settings", () => {
    cy.get("#repo-settings-tab").should('exist')
})

Then("The recent actions component is not visible", () => {
    cy.get("#recent-actions-container").should("not.be.visible")
})

Then("The recent actions component is visible", () => {
    cy.get("#recent-actions-container").should("be.visible")
})