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

// brokerURL: 'ws://localhost:9000/project',
let stompClient = null;

function connect() {
    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:9000/gs-guide-websocket',
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

    stompClient.activate();
}

function subscribe() {
    stompClient.subscribe('/topic/greetings', function (greeting) {
        console.log(greeting.body);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({ destination:"/app/hello", body: "test message" });
}

document.addEventListener('DOMContentLoaded', function() {
    connect();
})