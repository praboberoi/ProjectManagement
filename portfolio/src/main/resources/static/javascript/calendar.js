const PROJECT_ID = window.location.pathname.split('/').slice(-1)
const PROJECT_START_DATE = document.getElementById("startDate").value
const PROJECT_END_DATE = document.getElementById('endDate').value
const SPRINT_COLOURS = ['green', 'purple', 'darkSlateGrey', 'firebrick', 'mediumVioletRed', 'mediumSeaGreen', 'orangeRed']

/**
 * Renders the calendar onto the page with sprints
 * @param sprintList A list of all the sprints in the project
 */
function renderCalendar(sprintList) {
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        firstDay: 1,
        events: sprintList,
        validRange: {
            start: PROJECT_START_DATE,
            end: PROJECT_END_DATE
        }
    });
    calendar.render();
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
                allDay: true
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
