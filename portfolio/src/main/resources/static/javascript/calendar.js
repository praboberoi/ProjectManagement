const PROJECT_ID = window.location.pathname.split('/').slice(-1);
const PROJECT_START_DATE = document.getElementById("startDate").value;
const PROJECT_END_DATE = document.getElementById('endDate').value;
const SPRINT_COLOURS = ['green', 'purple', 'darkSlateGrey', 'firebrick', 'mediumVioletRed', 'mediumSeaGreen', 'orangeRed'];
const CALENDAR_ERROR = document.getElementById('calendarError');
const CALENDAR_EL = document.getElementById('calendar');
let calendar;

/**
 * Renders the calendar onto the page with sprints
 * @param sprintList A list of all the sprints in the project
 */
function renderCalendar(sprintList) {
    calendar = new FullCalendar.Calendar(CALENDAR_EL, {
        initialView: 'dayGridMonth',
        firstDay: 1,
        events: sprintList,
        eventResizableFromStart: true,
        validRange: {
            start: PROJECT_START_DATE,
            end: PROJECT_END_DATE
        },
        eventMouseEnter: function (info) {
            info.event.setProp("borderColor",  'black');
            makeEventEditable(info);
        },
        eventMouseLeave: function (info) {
            info.event.setProp("borderColor",  null);
            removeEventEditable(info);
        },
        eventResize: function(info) {
            editEventDuration(info);
        },
        eventDrop: function(info) {
            editEventDuration(info);
        }
    });
    calendar.render();
}

/**
 * Makes the event editable on the calendar.
 * @param info
 */
function makeEventEditable(info) {
    calendar.getEvents().forEach((event, i) => {
        event.setProp('editable', false)
    });

    info.event.setProp('editable', true)
}

/**
 * Makes the event uneditable on the calendar.
 * @param info
 */
 function removeEventEditable(info) {
    info.event.setProp('editable', false)
}

/**
 * Send an HTTP request to update the event's start and end date.
 * @param info
 */
function editEventDuration(info) {
    let event = info.event;
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.response == 'true') {
                CALENDAR_ERROR.hidden = true;
                CALENDAR_ERROR.innerText = "";
            } else {
                CALENDAR_ERROR.hidden = false;
                CALENDAR_ERROR.innerText = "An error occurred and the event could not be saved.";
                info.revert();
            }
        }
    }

    httpRequest.open('POST', '/sprint/' + event.id + '/editSprint')

    // Need to remove 1 day
    let endDate = new Date(event.end);
    endDate.setDate(endDate.getDate() - 1);

    var params = new FormData();
    params.append('startDate', event.start.getTime());
    params.append('endDate', endDate.getTime());

    httpRequest.send(params);
}

/**
 * Adds a day to the end date to include the final day of the sprint
 * @param endDate the end date of the sprint
 * @returns date The end date of the sprint with 1 more day as json
 */
function getEndDate(endDate) {
    let tempEndDate = new Date(endDate);
    tempEndDate.setDate(tempEndDate.getDate() + 1);
    return tempEndDate.toJSON();
}

/**
 * Converts json sprints into event objects to be rendered onto the calendar
 * @param sprintJson A json list of the sprints
 * @returns sprints A list of event objects from sprints
 */
function getSprintList(sprintJson) {
    const jsonSprintList = JSON.parse(sprintJson)
    let sprintList = []
    jsonSprintList.forEach((sprint, i) => {
        const tempEndDate = getEndDate(sprint.endDate);
        sprintList.push(
            {
                id: sprint.sprintId,
                title: sprint.sprintLabel + ": " + sprint.sprintName,
                start: sprint.startDate,
                end: tempEndDate,
                backgroundColor: SPRINT_COLOURS[i % SPRINT_COLOURS.length],
                overlap: false,
                allDay: true,
            }
        );
    });
    return sprintList;
}

/**
 * Runs when the page is loaded and requests all the projects sprints from the server
 */
document.addEventListener('DOMContentLoaded', function() {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            const sprintList = getSprintList(httpRequest.response);
            renderCalendar(sprintList);
        }
    }

    httpRequest.open('GET', '/project/' + PROJECT_ID + '/getAllSprints/')
    httpRequest.send()
});
