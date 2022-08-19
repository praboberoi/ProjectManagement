import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I select the create event button", ()=> {
    cy.get('#event-btn').click()
})

When("I create a new event", () => {
    cy.get('#event-name').type('New Event')
    cy.get('#eventStartDate').clear().type('25082022')
    cy.get('#eventEndDate').clear().type('26082022')
    cy.get('#eventFormSubmitButton').click()
})

Then("A new event is created with a success message", () => {
    cy.get('.alert-success').should('contain.text', 'Successfully Created New Event')
})

When("I type in an event name thats really long", ()=> {
    cy.get('#event-name').type('Shared lunch will all of the seng302 students to celebrate the end of the year')
})

Then('Event name validation is carried out', ()=> {
    cy.get('#eventNameError').should('have.text', 'Event name must be less than 50 characters')
})

When("The start date is after the end date", () => {
    cy.get('#event-name').type('New Event')
    cy.get('#eventStartDate').clear().type('28082022')
    cy.get('#eventEndDate').clear().type('26082022')

})

Then("Date validation is carried out", ()=> {
    cy.get('#startDateError').should('have.text', 'Start date must be before the end date')
    cy.get('#endDateError').should('have.text', 'End date must be after the start date')
})

// cypress
// open terminal
// 1. npm install
// 2. npx cypress open
// 3. e2e