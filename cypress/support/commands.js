// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
Cypress.Commands.add('login', (username, password) => {
    cy.visit('')
    cy.get('#username-field').type(username, {scrollBehavior:'center', delay: 0})
    cy.get('#password-field').type(password, {scrollBehavior:'center', delay: 0})
    cy.get('#submit').click()
    cy.getCookie("lens-session-token").then((cookie) => {
        cy.setCookie('lens-session-token', cookie.value)
    })
})
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })