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

Then("A repo is connected", () => {
    cy.contains("Connected to repo:").should('exist')
});

Then("I cannot see repo settings", () => {
    cy.get("#repo-settings-tab").should('not.exist')
})

Then("I can see repo settings", () => {
    cy.get("#repo-settings-tab").should('exist')
})

When("I set invalid repo settings", () => {
    cy.get("#repo-settings-tab").click()

    cy.get("#git-project-alias").clear().type("c")
    cy.get("#git-project-alias").clear()

    cy.get("#git-project-id").clear().type("13964")
    cy.get("#git-project-id").clear()

    cy.get("#git-host-address").clear().type("http")
});

Then("Repo error messages are shown", () => {
    cy.get("#gitProjectAliasError").should('have.text', "Project Alias field must be between 1 and 50 characters")

    cy.get("#gitProjectIdError").should('have.text', "Project ID field must be between 1 and 50 characters")

    cy.get("#gitHostAddressError").should('have.text', "Project host address must be a valid HTTP URL")
    cy.get("#git-host-address").clear()
    cy.get("#gitHostAddressError").should('have.text', "Project host address field must not be empty")

});