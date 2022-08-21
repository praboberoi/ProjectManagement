import { Given, When, Then } from "@badeball/cypress-cucumber-preprocessor";

When("I create a sprint {string}", (sprint) => {
    cy.get('#sprint-btn').click()
    cy.get('#sprint-name').clear().type(sprint)
    cy.get('#submit-btn').click()
});

When("I start to create a sprint", () => {
    cy.get('#sprint-btn').click()
});

When("I edit the sprint {string}", (sprint) => {
    cy.contains(sprint).parent('tr').find('#sprint-edit-btn').find('img').click()
});

When("I change the sprint's {word} to {string}", (field, value) => {
    cy.get('#sprint-' + field).invoke('attr', 'value', value)
})

When("I save the sprint", () => {
    cy.get('#submit-btn').click()
})

When("I delete the sprint {string}", (sprint) => {
    cy.contains(sprint).parent('tr').find('#sprint-del-btn').click()
    cy.get('#deleteSprint').find('button').click()
})

Then("Sprint {string} has label {string}", (sprintName, sprintLabel) => {
    cy.contains(sprintName).parent("tr").contains(sprintLabel)
});

Then("I am on the {word} project page", (project) => {
    cy.get("#name").contains(project)
});

Then("Create sprint button exists", () => {
    cy.get('#sprint-btn').find('img').should('have.attr', 'src').should('include','create-icon')
});

Then("The default sprint name is the sprint label", () => {
    let label;
    cy.get('#sprint-label').should((div) => {
        label = div.val()
    })

    cy.get('#sprint-name').should((div) => {
        expect(label).equal(div.val())
    })
});

Then("The sprint start date is {string}", (date) => {
    cy.get('#sprint-start-date').should('have.value', date)
});

Then("The sprint end date is {string}", (date) => {
    cy.get('#sprint-end-date').should('have.value', date)
});

Then('Sprint is edited successfully', () => {
    cy.get('.alert-success').should('contain.text', 'Successfully Updated')
})

Then("The sprint {string} doesn't exist", (sprint) => {
    cy.contains(sprint).should('not.exist')
})