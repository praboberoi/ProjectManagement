import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

// When("I call create group {string}", (groupName) => {
//     cy.getCookie("lens-session-token").then((cookie) => {
//         cy.setCookie('lens-session-token', cookie.value)
//     })
//     cy.get("#projectId").invoke('val').then((projectId) => {
//         cy.contains(event).parents('.event-card').find(".hiddenEventId").invoke('val').then((eventId) => {
//             cy.request('DELETE', Cypress.config().baseUrl + '/project/' + projectId + '/event/' + eventId + '/delete')
//         })
//     })
// })

// When("I call edit event on {string}", (event) => {
//     cy.getCookie("lens-session-token").then((cookie) => {
//         cy.setCookie('lens-session-token', cookie.value)
//     })
//     cy.get("#projectId").invoke('val').then((projectId) => {
//         cy.contains(event).parents('.event-card').find(".hiddenEventId").invoke('val').then((eventId) => {
//             cy.request('POST', Cypress.config().baseUrl + '/project/' + projectId + '/saveEvent?eventId=' + eventId + '&eventName=Cypress+Websocket+Edited+Event&startDate=2022-07-17T00%3A00&endDate=2022-07-20T00%3A00')
//         })
//     })
// })

When("I call create group {string}", (groupName) => {
    cy.getCookie("lens-session-token").then((cookie) => {
        cy.setCookie('lens-session-token', cookie.value)
    })

    cy.request('POST', Cypress.config().baseUrl + '/groups/?shortName=' + groupName.replaceAll(' ', '+') + '&longName=Long+group+name')
})

When("I call edit group {string} to {string}", (groupName, newGroupName) => {
    cy.getCookie("lens-session-token").then((cookie) => {
        cy.setCookie('lens-session-token', cookie.value)
    })

    cy.contains(groupName).parents('.card').find(".group-id").invoke('val').then((groupId) => {
        cy.request('POST', Cypress.config().baseUrl + '/groups/?groupId=' + groupId + '&shortName=' + newGroupName.replaceAll(' ', '+') + '&longName=Long+group+name')
    })
})