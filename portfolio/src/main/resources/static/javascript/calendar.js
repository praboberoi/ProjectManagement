const PROJECT_ID = window.location.pathname.split('/').slice(-1)

const calendarEl = document.getElementById('calendar');

function getAllEvents() {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            let responseList = []
            const jsonResponseList = JSON.parse(httpRequest.response)
            console.log(jsonResponseList)
            for (const sprint in jsonResponseList) {
                console.log(sprint[0])
                responseList.push(
                    {
                        id: sprint.sprintId,
                        title: sprint.sprintName,
                        start: sprint.startDate,
                        end: sprint.endDate
                    }
                )
            }
            console.log(responseList)
            const calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                events: responseList
            });
            calendar.render();

        } else {
            console.log(httpRequest.response)
        }
    }

    httpRequest.open('GET', '/project/' + PROJECT_ID + '/getAllSprints/')
    httpRequest.send()
}

getAllEvents()