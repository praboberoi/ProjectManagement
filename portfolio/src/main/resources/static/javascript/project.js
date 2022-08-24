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

function deletingEvent(eventId, eventName, apiPrefix, projectId) {
    if(apiPrefix === null)
        apiPrefix = ""
    document.getElementById('messageEvent').innerText =  `Are you sure you want to delete ${eventName}`;
    document.getElementById('deleteEvent').setAttribute('action', `${apiPrefix}/${projectId}/deleteEvent/${eventId}`);
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
    stompClient = new StompJs.Client({
        brokerURL: 'ws://' + window.location.host + apiPrefix + '/gs-guide-websocket',
        debug: function(str) {
            console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });
    
    stompClient.onConnect = function (frame) {
        console.log('Connected: ' + frame);
        subscribe()
    };

    stompClient.onStompError = function (frame) {
        console.log('Broker reported error: ' + frame.headers['message']);
        console.log('Additional details: ' + frame.body);
    }

    stompClient.activate();
}

/**
 * Subscribes to the required websocket notification channels
 */
function subscribe() {
    stompClient.subscribe('/topic/greetings', function (greeting) {
        console.log(greeting.body);
    });
}

/**
 * Disconnects from the websocket
 */
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

/**
 * Sends a message to the hello message receiver
 */
function sendName() {
    stompClient.publish({ destination:"/app/hello", body: "test message" });
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
    console.log(new Date().toISOString())
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