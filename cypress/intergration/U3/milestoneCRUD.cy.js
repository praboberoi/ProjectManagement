import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I select the milestones tab", () => {
    cy.get('#milestone-tab').click()
})

Given("I select Create Milestone", () => {
    cy.get('#milestone-btn').click()
})

When("I enter {string} milestone name and {string} date", (milestoneName, milestoneDate) => {
    cy.get('#milestone-name').clear().should('have.value', '')
    cy.get('#milestoneNameError').should('have.text', 'Milestone name must not be empty')
    cy.get('#milestone-name').type("fgerg😀",{ delay: 0 })
    cy.get('#milestoneNameError').should('have.text', "Milestone name must not contain an emoji")
    cy.get('#milestone-name').clear().type(milestoneName, { delay: 0 }).should('have.value', milestoneName)
    cy.get('#milestoneNameError').should('have.text', '')
    cy.get('#milestoneDate').clear().type(milestoneDate).should('have.value', milestoneDate)
})

When('I select save on the milestone form', () => {
    cy.get('#milestoneFormSubmitButton').click()
})

When("I select edit for {string} milestone", (milestone) => {
    cy.contains(milestone).parents('.milestone-card').contains("Edit").click()
})

When("I change the name to {string}", (milestoneName) => {
    cy.get('#milestone-name').clear().should('have.value', '')
    cy.get('#milestone-name').type(milestoneName, { delay: 0 }).should('have.value', milestoneName)
})

When("I select delete for {string} milestone", (milestone) => {
    cy.contains(milestone).parents('.milestone-card').contains("Delete").click()
})

When('I select delete again on the conformation modal', () => {
    cy.get('#deleteMilestoneModalBtn').click()
})

When("I enter an invalid milestone date", () => {
    cy.get('#milestoneDate').clear().type("2020-06-10", {delay:0})
})

Then("I am unable to click submit", () => {
    cy.get('#milestoneFormSubmitButton').should('be.disabled')
})

Then('A new milestone is created', () => {
    cy.contains("#messageSuccess", 'Successfully Created CypressTest1')
    cy.get('#milestone-tab').click()
    cy.get('.card-title').should('contain.text', 'CypressTest1')
})

Then("Milestone name is updated successfully to {string}", (milestoneName) => {
    cy.contains("#messageSuccess", `Successfully Updated ${milestoneName}`)
})

Then("{string} milestone is successfully deleted", (milestoneName) => {
    cy.contains("#messageSuccess", `Successfully deleted ${milestoneName}`)
})

Then("I can see I have used {string} characters", (charLeft) => {
    cy.contains('#milestoneCharCount', `${charLeft} `)
})

Then("An error message is displayed for the date being outside the project range", () => {
    cy.get('#milestoneDateError').should('have.text', "Milestone must start on or after 15 Jul 2022")
})

Then("The current date is the milestone date", () => {
    cy.get('#milestoneDate').should("have.value",
        new Date().toLocaleDateString("en-CA"))
})

When("I call create milestone {string}", (milestoneName) => {
    cy.getCookie("lens-session-token").then((cookie) => {
        cy.setCookie('lens-session-token', cookie.value)
    })

    cy.request('POST', Cypress.config().baseUrl + '/project/1000/milestone?milestoneId=0&name=' + milestoneName.replaceAll(' ', '+') + '&date=2022-09-24')
})