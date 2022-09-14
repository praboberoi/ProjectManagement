const SPRINT_COLOURS = ['blue', 'skyblue', 'purple', 'orange', 'green', 'pink', 'navy'];const CALENDAR_MESSAGE = document.getElementById('calendarMessage');
const CALENDAR_MESSAGE = document.getElementById('calendarMessage');
const CALENDAR_EL = document.getElementById('calendar');
const adminRoles = ['TEACHER', 'COURSE_ADMINISTRATOR'];
let calendar;
let clicked = true;

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
            start: projectStartDate,
            end: new Date(projectEndDate)
        },
        eventMouseEnter: function (info) {
            if (userRoles.some(role => adminRoles.indexOf(role) >= 0)) {
                makeEventEditable(info);
            }
        },
        eventMouseLeave: function (info) {
            if (userRoles.some(role => adminRoles.indexOf(role) >= 0)) {
                if (!info.event.extendedProps.clicked) {
                    removeEventEditable(info);
                }
            }
        },
        eventResize: function(info) {
            editEventDuration(info);
        },
        eventDrop: function(info) {
            editEventDuration(info);
        },
        eventClick: function(info) {
            if (userRoles.some(role => adminRoles.indexOf(role) >= 0)) {
                clicked = false;
                makeEventEditable(info);
                clicked = true;
            }
        }
    });
    calendar.render();
}

/**
 * Makes the event editable on the calendar.
 * @param info
 */
function makeEventEditable(info) {
    if (!clicked) {
        calendar.getEvents().forEach((event, i) => {
            event.setExtendedProp("clicked", false);
            event.setProp('editable', false)
        });
        info.event.setExtendedProp("clicked", true)
    }
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
            if (httpRequest.response == "") {
                let sprintStartDateElement = document.getElementById('sprint' + info.event.id + "StartDate");
                let sprintEndDateElement = document.getElementById('sprint' + info.event.id + "EndDate");
                let sprintStartDate = new Date(event.start);
                let sprintEndDate = new Date(event.end);
                sprintEndDate.setDate(sprintEndDate.getDate() - 1);
                sprintStartDateElement.innerText = sprintStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
                sprintEndDateElement.innerText = sprintEndDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
                CALENDAR_MESSAGE.hidden = false;
                CALENDAR_MESSAGE.classList.remove("alert-danger");
                CALENDAR_MESSAGE.classList.add("alert-success");
                CALENDAR_MESSAGE.innerText =  "Sprint saved successfully";
                loadEventCards();
                loadDeadlineCards();

            } else {
                CALENDAR_MESSAGE.hidden = false;
                CALENDAR_MESSAGE.classList.add("alert-danger");
                CALENDAR_MESSAGE.classList.remove("alert-success");
                CALENDAR_MESSAGE.innerText = httpRequest.response;
                info.revert();
            }
        }
    }

    httpRequest.open('POST', apiPrefix +'/sprint/' + event.id + '/editSprint')

    // Need to remove 1 day
    let endDate = new Date(event.end);
    endDate.setDate(endDate.getDate() - 1);
    const startDate = new Date(event.start)
    const startDateStr = startDate.toLocaleDateString().split('/').reverse().join('-');
    const endDateStr = endDate.toLocaleDateString().split('/').reverse().join('-');
    var params = new FormData();
    params.append('startDate', startDateStr);
    params.append('endDate', endDateStr);

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
                clicked: false
            }
        );
    });
    return sprintList;
}

/**
 * Requests all the projects sprints from the server and load calendar
 */
function loadCalendar() {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            const sprintList = getSprintList(httpRequest.response);
            renderCalendar(sprintList);
        }
    }

    httpRequest.open('GET', apiPrefix  + '/project/' + projectId + '/getAllSprints')
    httpRequest.send()
}
