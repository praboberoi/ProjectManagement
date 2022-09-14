import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I set valid repo settings", () => {
    cy.intercept("https://eng-git.canterbury.ac.nz/api/v4/projects/**", {
        statusCode: 200,
        body: {
            "id":13964,
            "description":"This is an example project to check if the gitlab connection is working correctly",
            "name":"seng302 test project"
        },
    })
    cy.intercept("https://eng-git.canterbury.ac.nz/api/v4/projects/*/events", {
        statusCode: 200,
        body: [{
            "id":1,
            "project_id":1,
            "action_name":"joined",
            "author_id":1,
            "created_at":"2022-09-11T16:43:19.178+12:00",
            "author":{
                "id":1,
                "name":"Cypress user",
                "username":"CYU",
                "avatar_url":"https://secure.gravatar.com/avatar/1",
            },
            "author_username":"CYU",
        }]
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
