const projectDescriptionNav = document.getElementById('projectDescriptionNav')
const plannerNav = document.getElementById('plannerNav')
const cal = document.getElementById('cal')
const projectDescription = document.getElementById('projectCard')
const sprintTable = document.getElementById('showSprints')
const sprintLabels = document.getElementById('sprintLabel')
const calendarElements = document.getElementById('calendarOb')
const eventLabel = document.getElementById('showEventLabel')

/**
 * Updates the conformation message to delete the sprint with appropriate sprint name
 */
function updateSprintDetails(sprintId, sprintName, projectId, prefix) {
    if (prefix === null)
        prefix = ""
    document.getElementById('message').innerText = `Are you sure you want to delete ${sprintName}`;
    document.getElementById('deleteSprint').setAttribute('action', `${prefix}/${projectId}/deleteSprint/${sprintId}`);
}

/**
 * Switches the current display from project details to the calender view
 */
function navTOProjectDescription() {
    if (projectDescriptionNav.ariaSelected === "false") {
        cal.hidden = true
        projectDescription.hidden = false
        projectDescriptionNav.ariaSelected = "true";
        plannerNav.ariaSelected = "false"
        plannerNav.classList.remove('active')
        projectDescriptionNav.classList.add('active')
        sprintTable.hidden = false
        sprintLabels.hidden = false
        calendarElements.hidden = false
        eventLabel.hidden = false

    }
}

/**
 * Switches the current display from project details to the planner
 */
function navToPlanner() {
    if (plannerNav.ariaSelected === "false") {
        cal.hidden = false
        projectDescription.hidden = true
        projectDescriptionNav.ariaSelected = "false";
        plannerNav.ariaSelected = "true"
        projectDescriptionNav.classList.remove('active')
        plannerNav.classList.add('active')
        sprintTable.hidden = true
        sprintLabels.hidden = true
        calendarElements.hidden = true
        eventLabel.hidden = true
        loadCalendar()
    }
}

function deletingEvent(eventId, eventName) {
    document.getElementById('messageEvent').innerText =  `Are you sure you want to delete ${eventName}`;
    document.getElementById('deleteEvent-btn').onclick = function() {
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
    document.getElementById('messageDeadline').innerText =  `Are you sure you want to delete ${deadlineName}`;
    document.getElementById('deleteDeadlineModalBtn').onclick = function() {
        let httpRequest = new XMLHttpRequest();
        httpRequest.onreadystatechange = () => processAction(httpRequest)
        httpRequest.open('DELETE', apiPrefix + `/${projectId}/deleteDeadline/${deadlineId}`);
        httpRequest.send();
    }
}

/**
 * Runs when the page is loaded and hide calendar
 */
document.addEventListener('DOMContentLoaded', function() {
    cal.hidden = true
    projectDescription.hidden = false
    projectDescriptionNav.ariaSelected = "true";
    plannerNav.ariaSelected = "false"
    plannerNav.classList.remove('active')
    projectDescriptionNav.classList.add('active')
    sprintTable.hidden = false
    sprintLabels.hidden = false
    calendarElements.hidden = false
    eventLabel.hidden = false
});

let stompClient = null;

/**
 * Connects to the websocket server
 */
function connect() {
    let websocketProtocol = window.location.protocol === 'http:'?'ws://':'wss://'
    stompClient = new StompJs.Client({
        brokerURL: websocketProtocol + window.location.host + apiPrefix + '/lensfolio-websocket',
        debug: function(str) {
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
    let httpRequest = new XMLHttpRequest();
    if (action === "edited") {
        sprintElement = document.getElementById("sprint-list")
        httpRequest.open('GET', window.location.pathname + `/sprints`);
    } else if (action === "deleted") {
        document.getElementById(sprint + "Row").outerHTML = ""
        return
    } else {
        console.log("Unknown command: " + action)
        return
    }
    httpRequest.onreadystatechange = () => updateElement(httpRequest, sprintElement)

    httpRequest.send();
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
    } else if (action === "deleted" && deadlineCard) {
        deadlineCard.outerHTML = ""
        return
    } else if (action === "editing" && deadlineCard) {
        let user = array[2]
        document.getElementById(deadline + '-notification').innerText =`${user} is currently editing`
        document.getElementById(deadline + '-edit-btn').hidden = true
        document.getElementById(deadline + '-delete-btn').hidden = true
    } else if (action === "finished" && deadlineCard) {
        document.getElementById(deadline + '-notification').innerText = ""
        document.getElementById(deadline + '-edit-btn').hidden = false
        document.getElementById(deadline + '-delete-btn').hidden= false
    } else {
        console.log("Unknown event or command: " + deadline + " " + action)
    }
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
    } else if (action === "deleted" && milestoneCard) {
        milestoneCard.outerHTML = ""
        return
    } else if (action === "editing" && milestoneCard) {
        let user = array[2]
        document.getElementById(milestone + '-notification').innerText =`${user} is currently editing`
        document.getElementById(milestone + '-edit-btn').hidden = true
        document.getElementById(milestone + '-delete-btn').hidden = true
    } else if (action === "finished" && milestoneCard) {
        document.getElementById(milestone + '-notification').innerText = ""
        document.getElementById(milestone + '-edit-btn').hidden = false
        document.getElementById(milestone + '-delete-btn').hidden= false
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
    } else if (action === "deleted" && eventCard) {
        eventCard.outerHTML = ""
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
document.addEventListener('DOMContentLoaded', function() {
    connect();
})

/**
 * Replaces the old http component with the new one contained in the request
 * @param httpRequest Request containing a model view element
 * @param element The element to replace
 * @param errorMessage Optional variable, changes the default error message location
 */
function updateElement(httpRequest, element, errorMessage = messageDanger){
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        errorMessage.innerText = ""
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
 function processAction(httpRequest){
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
        if (httpRequest.status === 200) {
            modalError.innerText = ""
            messageSuccess.innerText = httpRequest.responseText;
            modal.hide()
        } else if (httpRequest.status === 500) {
            messageSuccess.innerText = ""
            modalError.innerText = "An error occurred on the server, please try again later";
        } else if (httpRequest.status == 400) {
            messageSuccess.innerText = ""
            modalError.innerText = httpRequest.responseText;
        } else {
            messageSuccess.innerText = ""
            modalError.innerText = "Something went wrong.";
        }
    }
}