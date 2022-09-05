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
    document.getElementById('deleteDeadline').setAttribute('action', `${apiPrefix}/${projectId}/deleteDeadline/${deadlineId}`);
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
        console.log('Active updating enabled');
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
    loadEventCards()
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
        element = document.getElementById("sprint-list")
        httpRequest.open('GET', window.location.pathname + `/sprints`);
    } else if (action === "deleted") {
        document.getElementById(sprint + "Row").outerHTML = ""
        return
    } else {
        console.log("Unknown command: " + action)
        return
    }
    httpRequest.onreadystatechange = () => updateElement(httpRequest, element)

    httpRequest.send();
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
        document.getElementById(event + '-notification').innerText = user + " is currently editing."
        document.getElementById(event + '-edit-btn').disabled = true
        document.getElementById(event + '-delete-btn').disabled = true
    } else if (action === "finished" && eventCard) {
        document.getElementById(event + '-notification').innerText = ""
        document.getElementById(event + '-edit-btn').disabled = false
        document.getElementById(event + '-delete-btn').disabled = false
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
 * Updates the deadline modal form to edit the chosen deadline and shows the modal
 */
function editDeadline(name, date, id) {
    document.getElementById('deadlineFormSubmitButton').disabled = false;
    document.getElementById('deadline-name').classList.remove("formError");
    document.getElementById('deadlineNameError').innerText = null;
    document.getElementById('deadlineDateError');
    document.getElementById('deadline-name').value = name;
    document.getElementById('deadlineId').value = id;
    document.getElementById('deadlineCharCount').value = name.length;
    document.getElementById('deadlineDate').value = date;
    document.getElementById('deadlineFormTitle').textContent = "Edit deadline";
    const modalElement = document.getElementById('deadlineFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();
}

/**
 * Closes the modal
 */
function closeDeadlineModal() {
    const modalElement = document.getElementById('deadlineFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
    modal.hide();
}

/**
 *  Updates the deadline modal form to create a new deadline and shows the modal
 */
function createDeadline() {
    document.getElementById('deadline-name').classList.remove("formError");
    document.getElementById('deadlineNameError').innerText = null;
    document.getElementById('deadlineFormSubmitButton').disabled = false;
    document.getElementById('deadline-name').value = "New Deadline";
    document.getElementById('deadlineCharCount').value = "12";
    document.getElementById('deadlineDate').value = new Date().toLocaleDateString().split('/').reverse().join('-') + 'T00:00';
    document.getElementById('deadlineFormTitle').textContent = "Create New Deadline";
    const modalElement = document.getElementById('deadlineFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();


}

/**
 * Replaces the old http component with the new one contained in the request
 * @param httpRequest Request containing a model view element
 * @param element The element to replace
 */
function updateElement(httpRequest, element){
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            element.innerHTML = httpRequest.responseText;
        } else if (httpRequest.status === 400) {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = "Bad Request";
        } else {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = "Something went wrong.";
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

    element = document.getElementById("event-list")
    httpRequest.open('GET', window.location.pathname + `/events`);
    httpRequest.onreadystatechange = () => updateElement(httpRequest, element)

    httpRequest.send();
}
