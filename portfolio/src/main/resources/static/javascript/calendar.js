const PROJECT_ID = window.location.pathname.split('/').slice(-1)
const PROJECT_START_DATE = document.getElementById("startDate").value
const PROJECT_END_DATE = document.getElementById('endDate').value
const SPRINT_COLOURS = ['green', 'purple', 'darkSlateGrey', 'firebrick', 'mediumVioletRed', 'mediumSeaGreen', 'orangeRed']

document.addEventListener('DOMContentLoaded', function() {
    let httpRequest = new XMLHttpRequest();
    let responseList = []
    httpRequest.onreadystatechange = function (){
        console.log(httpRequest)
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            const jsonResponseList = JSON.parse(httpRequest.response)
            jsonResponseList.forEach((sprint, i) => {
                let tempEndDate = new Date(sprint.endDate);
                tempEndDate.setDate(tempEndDate.getDate() + 1)
                tempEndDate.toJSON()
                responseList.push(
                    {
                        id: sprint.sprintId,
                        title: sprint.sprintLabel + ": " + sprint.sprintName,
                        start: sprint.startDate,
                        end: tempEndDate,
                        backgroundColor: SPRINT_COLOURS[i % SPRINT_COLOURS.length],
                        overlap: false,
                        allDay: true
                    }
                )
            })
            const calendarEl = document.getElementById('calendar');
            const calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                firstDay: 1,
                events: responseList,
                validRange: {
                    start: PROJECT_START_DATE,
                    end: PROJECT_END_DATE
                }
            });
            calendar.render();
        } else {
        }
    }

    httpRequest.open('GET', '/project/' + PROJECT_ID + '/getAllSprints/')
    httpRequest.send()
});

