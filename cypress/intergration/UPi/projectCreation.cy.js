import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I select Create Project", () => {
    cy.get('#create-project-btn').click()
})

When("I enter {word} as a Project Name", (word) => {
    cy.get('#project-name').clear().type(word, {delay:0})

})

When("I enter {string} as the project start date", (date) => {
    cy.get('#projectFormStartDate').type(date, {delay:0})
})

When("I enter {string} as the project end date", (date) => {
    cy.get('#projectFormEndDate').type(date, {delay:0})
})

When("I enter {string} as the project description", (description) => {
    cy.get('#projectFormDescription').clear().type(description);
})

When("I enter a emojis for the project name and description", () => {
    cy.get('#project-name').clear().type('idsad😀')
    cy.get('#projectFormDescription').clear().type('sudsiaudnasi😀');
})
Then("Error message is displayed for using emojis for both project name and description", () => {
    cy.get('#projectNameError').should('have.text',"Project name must not contain an emoji");
    cy.get('#descriptionError').should('have.text',"Project description must not contain an emoji");
})

When("I click on Create button", () => {
    cy.get('#projectFormSubmitButton').click()
})

Then("I am redirected to {string} URL", (url) => {
    cy.url().should('include', url)
})

Then("A new Project with {word} is created", (word) => {
    cy.contains(word)
})

Then("I am unable to create the project", () => {
    cy.get('#projectFormSubmitButton').should('be.disabled')
})

Then("{string} error message is displayed under the start date", (errorMessage) => {
    cy.get('#startDateError').should('have.text',errorMessage )
})

Then("{string} error message is displayed under the end date", (errorMessage) => {
    cy.get('#endDateError').should('have.text',errorMessage )
})



