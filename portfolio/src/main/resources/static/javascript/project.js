

/**
 * Updates the conformation message to delete the sprint with appropriate sprint name
 */
function updateSprintDetails(sprintId, sprintName, projectId, prefix) {
    if (prefix === null)
        prefix = ""
    document.getElementById('message').innerText = `Are you sure you want to delete ${sprintName}`;
    document.getElementById('deleteSprint').setAttribute('action', `${prefix}/project/${projectId}/deleteSprint/${sprintId}`);
}

function deletingEvent(eventId, eventName) {
    document.getElementById('messageEvent').innerText = `Are you sure you want to delete ${eventName}`;
    document.getElementById('deleteEvent-btn').onclick = function () {
        deleteEvent(eventId);
    }
}

/**
 * Calls the server to delete the selected event and provide an error message on failure
 * @param eventId Id of the group to delete
 */
function deleteEvent(eventId) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = () => processAction(httpRequest)

    httpRequest.open('DELETE', apiPrefix + `/project/${projectId}/event/${eventId}/delete`);
    httpRequest.send();
}

/**
 * Calls the server to delete the selected event and provide an error message on failure
 * @param eventId Id of the group to delete
 */
function saveEvent() {
    let httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = () => processAction(httpRequest)

    httpRequest.open('POST', apiPrefix + `/project/${projectId}/saveEvent`);

    let formData = new FormData(document.forms.eventForm)

    httpRequest.send(formData);
}

/**
 * Updates the conformation message and action of the form to delete the deadline
 */
function deleteDeadline(deadlineId, deadlineName) {
    document.getElementById('messageDeadline').innerText = `Are you sure you want to delete ${deadlineName}`;
    document.getElementById('deleteDeadlineModalBtn').onclick = function () {
        let httpRequest = new XMLHttpRequest();
        httpRequest.onreadystatechange = () => processAction(httpRequest)
        httpRequest.open('DELETE', apiPrefix + `/project/${projectId}/deadline/${deadlineId}/delete`);
        httpRequest.send();
    }
}

/**
 * Updates the conformation message and action of the form to delete the milestone
 */
function deleteMilestone(milestoneId, milestoneName) {
    document.getElementById('messageMilestone').innerText = `Are you sure you want to delete ${milestoneName}`;
    document.getElementById('deleteMilestoneModalBtn').onclick = function() {
        let httpRequest = new XMLHttpRequest();
        httpRequest.onreadystatechange = () => processAction(httpRequest)
        httpRequest.open('DELETE', apiPrefix + `/project/${projectId}/milestone/${milestoneId}/delete`);
        httpRequest.send();
    }
}

let stompClient = null;

/**
 * Connects to the websocket server
 */
function connect() {
    let websocketProtocol = window.location.protocol === 'http:' ? 'ws://' : 'wss://'
    stompClient = new StompJs.Client({
        brokerURL: websocketProtocol + window.location.host + apiPrefix + '/lensfolio-websocket',
        debug: function (str) {
            // console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = function () {
        subscribe()
        document.getElementById("websocket-status").value = "connected"
    };

    stompClient.onStompError = function () {
        console.log('Websocket communication error')
    }

    stompClient.activate();
}

/**
 * Subscribes to the required websocket notification channels
 */
function subscribe() {
    stompClient.subscribe('/element/project/' + projectId + '/sprints', updateSprint);
    stompClient.subscribe('/element/project/' + projectId + '/events', handleEventNotification);
    stompClient.subscribe('/element/project/' + projectId + '/deadlines', handleDeadlineNotification);
    stompClient.subscribe('/element/project/' + projectId + '/milestones', handleMilestoneNotification);
    loadEventCards()
    loadDeadlineCards()
    loadMilestoneCards()
}

/**
 * Replaces the relevant component of the sprint table
 * @param message Message with sprint and edit type
 */
function updateSprint(message) {
    let array = message.body.split(' ')
    let sprint = array[0]
    let action = array[1]

    updateSprintAccordions()

    let httpRequest = new XMLHttpRequest();
    if (action === "edited") {
        let sprintElement = document.getElementById("sprint-list")
        httpRequest.open('GET', window.location.pathname + `/sprints`);
        httpRequest.onreadystatechange = () => updateElement(httpRequest, sprintElement)
    
        httpRequest.send();
    } else if (action === "deleted") {
        document.getElementById(sprint + "Row").outerHTML = ""
    } else {
        console.log("Unknown command: " + action)
    }
    
}

/**
 * Handles deadline updates from the server
 * @param message Message with deadline and edit type
 */
function handleDeadlineNotification(message) {
    let array = message.body.split(' ')
    let deadline = array[0]
    let action = array[1]

    let deadlineCard = document.getElementById(deadline + "-card");

    if (action === "edited") {
        loadDeadlineCards()
        updateSprintAccordions()
    } else if (action === "deleted" && deadlineCard) {
        deadlineCard.outerHTML = ""
        updateSprintAccordions()
        return
    } else if (action === "editing" && deadlineCard) {
        let user = array[2]
        document.getElementById(deadline + '-notification').innerText = `${user} is currently editing`
        document.getElementById(deadline + '-edit-btn').hidden = true
        document.getElementById(deadline + '-delete-btn').hidden = true
    } else if (action === "finished" && deadlineCard) {
        document.getElementById(deadline + '-notification').innerText = ""
        document.getElementById(deadline + '-edit-btn').hidden = false
        document.getElementById(deadline + '-delete-btn').hidden = false
    } else {
        console.log("Unknown event or command: " + deadline + " " + action)
    }
}

function updateSprintAccordions() {
    let httpRequest = new XMLHttpRequest();
    let accordions = document.getElementById("showSprints").getElementsByClassName("accordion-body")

    Array.from(accordions).forEach(element => {
        let sprintId = element.parentNode.getElementsByClassName("accordion-sprint-id")[0].value
        httpRequest.open('GET', window.location.pathname + `/sprint/${sprintId}/accordion`);
        httpRequest.onreadystatechange = () => updateElement(httpRequest, element.parentNode)
    
        httpRequest.send();
    });
    
}


/**
 * Handles milestone updates from the server
 * @param message Message with milestone and edit type
 */
function handleMilestoneNotification(message) {
    let array = message.body.split(' ')
    let milestone = array[0]
    let action = array[1]

    let milestoneCard = document.getElementById(milestone + "-card");

    if (action === "edited") {
        loadMilestoneCards()
        updateSprintAccordions()
    } else if (action === "deleted" && milestoneCard) {
        milestoneCard.outerHTML = ""
        updateSprintAccordions()
        return
    } else if (action === "editing" && milestoneCard) {
        let user = array[2]
        document.getElementById(milestone + '-notification').innerText = `${user} is currently editing`
        document.getElementById(milestone + '-edit-btn').hidden = true
        document.getElementById(milestone + '-delete-btn').hidden = true
    } else if (action === "finished" && milestoneCard) {
        document.getElementById(milestone + '-notification').innerText = ""
        document.getElementById(milestone + '-edit-btn').hidden = false
        document.getElementById(milestone + '-delete-btn').hidden = false
    } else {
        console.log("Unknown event or command: " + milestone + " " + action)
    }
}

/**
 * Handles event updates from the server
 * @param message Message with event and edit type
 */
function handleEventNotification(message) {
    let array = message.body.split(' ')
    let event = array[0]
    let action = array[1]

    let eventCard = document.getElementById(event + "-card");

    if (action === "edited") {
        loadEventCards()
        updateSprintAccordions()
    } else if (action === "deleted" && eventCard) {
        eventCard.outerHTML = ""
        updateSprintAccordions()
        return
    } else if (action === "editing" && eventCard) {
        let user = array[2]
        document.getElementById(event + '-notification').innerText = user + " is currently editing"
        document.getElementById(event + '-edit-btn').hidden = true
        document.getElementById(event + '-delete-btn').hidden = true
    } else if (action === "finished" && eventCard) {
        document.getElementById(event + '-notification').innerText = ""
        document.getElementById(event + '-edit-btn').hidden = false
        document.getElementById(event + '-delete-btn').hidden = false
    } else {
        console.log("Unknown event or command: " + event + " " + action)
    }
}

/**
 * Runs the connect function when the document is loaded
 */
document.addEventListener('DOMContentLoaded', function () {
    connect();
})

document.getElementById('planner-tab').addEventListener('shown.bs.tab', loadCalendar);

/**
 * Replaces the old http component with the new one contained in the request
 * @param httpRequest Request containing a model view element
 * @param element The element to replace
 * @param errorMessage Optional variable, changes the default error message location
 */
function updateElement(httpRequest, element, errorMessage = messageDanger) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            element.innerHTML = httpRequest.responseText;
        } else if (httpRequest.status === 400) {
            errorMessage.innerText = "Bad Request";
        } else if (httpRequest.status === 404) {
            errorMessage.innerText = "Unable to load " + element.id;
        } else {
            errorMessage.innerText = "Something went wrong.";
        }
    }
}

/**
 * Replaces the old messages with the new one contained in the request
 * @param httpRequest Request containing a model view element
 */
function processAction(httpRequest) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            messageSuccess.hidden = false
            messageDanger.hidden = true;
            messageSuccess.innerText = httpRequest.responseText;
        } else if (httpRequest.status === 400) {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = httpRequest.responseText;
        } else {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = "Something went wrong.";
        }
    }
}

/**
 * Loads the list of events cards under the event tab
 */
function loadEventCards() {
    let httpRequest = new XMLHttpRequest();

    let eventElement = document.getElementById("event-list")
    httpRequest.open('GET', window.location.pathname + `/events`);
    httpRequest.onreadystatechange = () => updateElement(httpRequest, eventElement)

    httpRequest.send();
}

/**
 * Loads the list of deadlines cards under the event tab
 */
function loadDeadlineCards() {
    let httpRequest = new XMLHttpRequest();

    let deadlineElement = document.getElementById("deadline-list")
    httpRequest.open('GET', window.location.pathname + `/deadlines`);
    httpRequest.onreadystatechange = () => updateElement(httpRequest, deadlineElement)
    httpRequest.send();
}

/**
 * Loads the list of milestone cards under the event tab
 */
function loadMilestoneCards() {
    let httpRequest = new XMLHttpRequest();

    let milestoneElement = document.getElementById("milestone-list")
    httpRequest.open('GET', window.location.pathname + `/milestones`);
    httpRequest.onreadystatechange = () => updateElement(httpRequest, milestoneElement)
    httpRequest.send();
}

/**
 * Updates the error message and removes the modal if there is no issues
 * @param httpRequest Request made to the server
 * @param modal Which modal is being edited
 * @param modalError Error message div that displays an error
 */
function updateModal(httpRequest, modal, modalError) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        console.log(httpRequest.status)
        console.log(httpRequest.responseText)
        if (httpRequest.status === 200) {
            modalError.innerText = ""
            messageSuccess.innerText = httpRequest.responseText;
            modal.hide()
        } else if (httpRequest.status === 500) {
            messageSuccess.innerText = ""
            modalError.innerText = "An error occurred on the server, please try again later";
        } else if (httpRequest.status === 400) {
            messageSuccess.innerText = ""
            modalError.innerText = httpRequest.responseText;
        } else {
            messageSuccess.innerText = ""
            modalError.innerText = "Something went wrong.";
        }
    }
}