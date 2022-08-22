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
    const currentDate = new Date()
    // const longerThanYear = new Date(currentDate.getFullYear() - 10, currentDate.getMonth(), currentDate.getDate())
    // cy.get('#startDate').type(`${longerThanYear.toISOString().substring(0,10)}`)
    // cy.get('#startDateError').should('have.text','Project must have started in the last year.' )
    cy.get('#startDate').type(date)
    cy.get('#startDateError').should('have.text','' )

    // const afterEndDate = new Date(currentDate.getFullYear() + 2, currentDate.getMonth(), currentDate.getDate())
    // cy.get('#startDate').type(`${afterEndDate.toISOString().substring(0,10)}`)
    // cy.get('#startDateError').should('have.text','Start date must be before the end date.' )
    // cy.get('#endDateError').should('have.text','End date must be after the start date' )
    // cy.get('#startDate').type(date)
    // cy.get('#endDateError').should('have.text','' )
    // cy.get('#startDateError').should('have.text','' )

})

When("I enter {string} as the project end date", (date) => {
    // const currentDate = new Date()
    // const longerThanTenYears = new Date(currentDate.getFullYear() + 12, currentDate.getMonth(), currentDate.getDate())
    // cy.get('#endDate').type(`${longerThanTenYears.toISOString().substring(0,10)}`)
    // cy.get('#endDateError').should('have.text','Project must end in the next 10 years.' )
    cy.get('#endDate').type(date)
    cy.get('#endDateError').should('have.text','' )

    // const beforeStartDate = new Date(currentDate.getFullYear() - 4, currentDate.getMonth(), currentDate.getDate())
    // cy.get('#endDate').type(`${beforeStartDate.toISOString().substring(0,10)}`)
    // cy.get('#startDateError').should('have.text','Start date must be before the end date.' )
    // cy.get('#endDateError').should('have.text','End date must be after the start date' )
    // cy.get('#EndDate').type(date)
    // cy.get('#endDateError').should('have.text','' )
    // cy.get('#startDateError').should('have.text','' )


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


