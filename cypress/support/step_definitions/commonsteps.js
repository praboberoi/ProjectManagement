import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

Given("I login as an admin", () => {
    cy.login("CypressAdmin", "CypressUser1")
});

Given("I login as a student", () => {
    cy.login("CypressStudent", "CypressUser1")
});

Given("I navigate to {string}", (location) => {
    cy.contains(".nav-link", location).click()
    if (location == "Groups") {
        waitForWebSocket()
    }
});

Given("I navigate directly to {string}", (location) => {
    cy.visit(location)
});

Given("I select group {string}", (group) => {
    cy.intercept({
        method: 'GET',
        url: '/groups/*',
    }).as('groupCheck')

    cy.get('.project-card a').contains(group).click()
    cy.wait('@groupCheck')
});

Given("I navigate to group {string}", (group) => {
    cy.intercept({
        method: 'GET',
        url: '/groups/*',
    }).as('groupCheck')

    cy.get('.project-card a').contains(group).click()
    cy.wait('@groupCheck')

    cy.get("#group-settings-icon").click()
    cy.contains("Navigate To Group").click()

})

When("I select the {word} project", (project) => {
    cy.contains(project).click()
    waitForWebSocket()
})

/**
 * Waits for the websocket to subscribe to the required channels (Might move to commands if websockets gets used more)
 * @param {number} counter
 */
function waitForWebSocket(counter=0) {
    cy.get('#websocket-status').then((el) => {
        if (counter > 5) {
            throw new Error(
            `Websockets not connected`
            );
        // if the element still says loading, wait 500ms and recurse
        } else if (el.val() !== 'connected') {
            cy.wait(100)
            return waitForWebSocket(counter + 1)
        }
    });
}

Then("{string} doesn't exist", (string) => {
    cy.contains(string).should('not.exist')
})

Then("{string} exists", (string) => {
    cy.contains(string).should('exist')
})

Then("I am directed to {string} URL", (url) => {
    cy.url().should('include', url)
})