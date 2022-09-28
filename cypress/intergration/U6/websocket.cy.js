
import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

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

When("I call edit current group to {string}", (newGroupName) => {
    cy.getCookie("lens-session-token").then((cookie) => {
        cy.setCookie('lens-session-token', cookie.value)
    })

    cy.intercept('/group/**').as('groupTitle')
    cy.get('input[name="groupId"]').invoke('val').then((groupId) => {
        cy.request('POST', Cypress.config().baseUrl + '/groups/?groupId=' + groupId + '&shortName=' + newGroupName.replaceAll(' ', '+') + '&longName=Long+group+name+' + groupId)
        cy.wait('@groupTitle')
    })
})

 

When("I call delete group {string}", (groupName) => {
    cy.getCookie("lens-session-token").then((cookie) => {
        cy.setCookie('lens-session-token', cookie.value)
    })

    cy.contains(groupName).parents('.card').find(".group-id").invoke('val').then((groupId) => {
        cy.request('DELETE', Cypress.config().baseUrl + '/groups/' + groupId)
    })
})