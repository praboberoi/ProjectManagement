import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I call delete event on {string}", (event) => {
    cy.getCookie("lens-session-token").then((cookie) => {
        cy.setCookie('lens-session-token', cookie.value)
    })
    cy.get("#projectId").invoke('val').then((projectId) => {
        cy.contains(event).parents('.event-card').find(".hiddenEventId").invoke('val').then((eventId) => {
            cy.request('DELETE', Cypress.config().baseUrl + '/project/' + projectId + '/event/' + eventId + '/delete')
        })
    })
})

When("I call edit event on {string}", (event) => {
    cy.getCookie("lens-session-token").then((cookie) => {
        cy.setCookie('lens-session-token', cookie.value)
    })
    cy.get("#projectId").invoke('val').then((projectId) => {
        cy.contains(event).parents('.event-card').find(".hiddenEventId").invoke('val').then((eventId) => {
            cy.request('POST', Cypress.config().baseUrl + '/project/' + projectId + '/saveEvent?eventId=' + eventId + '&eventName=Cypress+Websocket+Edited+Event&startDate=2022-06-17T00%3A00&endDate=2022-07-20T00%3A00')
        })
    })
})

When("I call create event on {string}", (event) => {
    cy.getCookie("lens-session-token").then((cookie) => {
        cy.setCookie('lens-session-token', cookie.value)
    })
    cy.get("#projectId").invoke('val').then((projectId) => {
        cy.request('POST', Cypress.config().baseUrl + '/project/' + projectId + '/saveEvent?eventId=0&eventName=' + event.replaceAll(' ', '+') + '&startDate=2022-06-17T00%3A00&endDate=2022-07-20T00%3A00')

    })
})