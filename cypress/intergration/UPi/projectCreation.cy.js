import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I select Create Project", () => {
    cy.get('#create-btn').click()
})

When("I enter {word} as a Project Name", (word) => {
    cy.get('#project-name').should('have.value', `Project ${new Date().getFullYear()}`)
    cy.get('#project-name').clear()
    cy.get('#projectNameError').should('have.text','Project name must not be empty or greater than 32 characters')
    cy.get('#project-name').type('😀')
    cy.get('#projectNameError').should('have.text',"Project name must not contain an emoji");
    cy.get('#project-name').clear().type(word)


})

When("I enter {string} as the project start date", (date) => {
    cy.get('#startDate').type(date, {delay:0})
})

When("I enter {string} as the project end date", (date) => {
    cy.get('#endDate').type(date, {delay:0})
})

When("I enter {string} as the project description", (description) => {
    cy.get('#projectDescription').clear().type('😀');
    cy.get('#descriptionError').should('have.text',"Project description must not contain an emoji");
    cy.get('#projectDescription').clear().type(description);
})

When("I click on Create button", () => {
    cy.get('#save-submit-button').click()
})

Then("I am redirected to {string} URL", (url) => {
    cy.url().should('include', url)
})

Then("A new Project with {word} is created", (word) => {
    cy.contains(word)
})

Then("I am unable to create the project", () => {
    cy.url().should('include', "/dashboard/newProject")
})

Then("{string} error message is displayed under the start date", (errorMessage) => {
    cy.get('#startDateError').should('have.text',errorMessage )
})

Then("{string} error message is displayed under the end date", (errorMessage) => {
    cy.get('#endDateError').should('have.text',errorMessage )
})



