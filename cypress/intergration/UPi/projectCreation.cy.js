import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I select Create Project", () => {
    cy.get('#create-btn').click()
})

When("I enter {word} as a Project Name", (word) => {
    cy.get('#project-name').should('have.value', `Project ${new Date().getFullYear()}`)
    cy.get('#project-name').clear()
    cy.get('#projectNameError').should('have.text','Project Name must not be empty or greater than 32 characters')
    cy.get('#project-name').type(word)
    cy.get('#projectNameError').should('have.text',"")

})

When("I enter {string} as the project start date", (date) => {
    cy.get('#startDate').type(date)
    cy.get('#startDateError').should('have.text','' )
})

When("I enter {string} as the project end date", (date) => {
    cy.get('#endDate').type(date)
    cy.get('#endDateError').should('have.text','' )
})

When("I enter {string} as the project description", (description) => {
    cy.get('#projectDescription').type(description)
})

When("I click on Create button", () => {
    cy.get('#save-submit-button').click()
})

Then("I am directed to {string} URL", (url) => {
    cy.url().should('include', url)
    cy.visit("/logout")
})

Then("I am redirected to {string} URL", (url) => {
    cy.url().should('include', url)
})

Then("A new Project with {word} is created", (word) => {
    cy.contains(word)
    cy.get(`[data-name="${word}"]`).click()
    cy.get('#deleteProject').children().contains(' Delete').click()
    cy.visit("/logout")
})

Then("I am unable to create the project", () => {
    cy.url().should('include', "/dashboard/newProject")
})

Then("{string} error message is displayed under the start date", (errorMessage) => {
    cy.get('#startDateError').should('have.text',errorMessage )
})

Then("{string} error message is displayed under the end date", (errorMessage) => {
    cy.get('#endDateError').should('have.text',errorMessage )
    cy.visit("/logout")
})



