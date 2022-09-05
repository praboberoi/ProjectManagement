import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I select the create event button", ()=> {
    cy.get('#create-event-btn').click()
})

When("I create a new event {string}", (event) => {
    cy.get('#event-name').clear().type(event, {"delay":0})
    cy.get('#eventFormSubmitButton').click()
})

Then("The new event {string} is created with a success message", (event) => {
    cy.get('.alert-success').should('contain.text', 'Successfully Created ' + event)
})


When("I edit the event {string}",(event) => {
    cy.contains(event).parents('.event-card').contains('Edit').find('img').click()
    cy.get('#event-name').clear().type('Updated Coding Competition', {"delay":0})
    cy.get('#eventFormSubmitButton').click()
})

Then("The event name is successfully updated to {string}",(name) => {
    cy.get('.alert-success').should('contain.text', 'Successfully Saved ' + name)
})

When("I type in an event name {string}", (name)=> {
    cy.get('#event-name').clear().type(name, {"delay":0})
})

Then('I am told I have used {string} characters', (number)=> {
    cy.get('#charCount').should('have.text', number )
})

When("I type in an event name that is very long", ()=> {
    cy.get('#event-name').clear().type('Shared lunch will all of the seng302 students to celebrate the end of the year', {"delay":0})
})

Then('An error message is displayed showing that the event name has exceeded the maximum characters', ()=> {
    cy.get('#eventNameError').should('have.text', 'Event Name cannot exceed 50 characters')
})


When("The event start date is outside the project date", () => {
    cy.get('#event-name').clear().type('New Event', {"delay":0})
    cy.get('#eventStartDate').clear().type('2022-06-10T08:30')
    cy.get('#eventEndDate').clear().type('2022-12-25T08:30')
})

Then("An error message is shown with the valid event dates", ()=> {
    cy.get('#eventStartDateError').should('have.text', 'Event must start on or after the 15 Jul 2022')
})

When("I enter the valid date and times for the event", () => {
    cy.get('#event-name').clear().type('New Event', {"delay":0})
    cy.get('#eventStartDate').clear().type('2022-07-30T08:30')
    cy.get('#eventEndDate').clear().type('2022-08-25T08:30')
    cy.get('#eventFormSubmitButton').click()
})

Then("The event is successfully saved", ()=> {
    cy.get('.alert-success').should('contain.text', 'Successfully Created New Event')
})
