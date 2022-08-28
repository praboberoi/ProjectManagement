import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I select the create event button", ()=> {
    cy.get('#event-btn').click()
})

When("I create a new event", () => {
    cy.get('#event-name').click().clear().type('Coding Competition', {"delay":1})
    cy.get('#eventFormSubmitButton').click()
})

Then("A new event is created with a success message", () => {
    cy.get('.alert-success').should('contain.text', 'Successfully Created Coding Competition', {"delay":1})
})


When("I type in an event name", ()=> {
    cy.get('#event-name').click().clear().type('Tech Meeting', {"delay":1})
})

Then('I am told how many characters I have used', ()=> {
    cy.get('#charCount').should('have.text', '12 ')
})

When("I type in an event name that is very long", ()=> {
    cy.get('#event-name').click().clear().type('Shared lunch will all of the seng302 students to celebrate the end of the year', {"delay":1})
})

Then('An error message is displayed showing that the event name has exceeded the maximum characters', ()=> {
    cy.get('#eventNameError').should('have.text', 'Event Name cannot exceed 50 characters')
})


When("The event start date is outside the project date", () => {
    cy.get('#event-name').click().clear().type('New Event')
    cy.get('#eventStartDate').click().clear().type('2022-06-10T08:30')
    cy.get('#eventEndDate').click().clear().type('2022-12-25T08:30')
})

Then("An error message is shown with the valid event dates", ()=> {
    cy.get('#eventStartDateError').should('have.text', 'Event must start on or after the 15 Jul 2022')
})

When("I enter the valid date and times for the event", () => {
    cy.get('#event-name').click().clear().type('New Event')
    cy.get('#eventStartDate').click().clear().type('2022-07-30T08:30')
    cy.get('#eventEndDate').click().clear().type('2022-08-25T08:30')
    cy.get('#eventFormSubmitButton').click()
})

Then("The event is successfully saved", ()=> {
    cy.get('.alert-success').should('contain.text', 'Successfully Created New Event')
})
