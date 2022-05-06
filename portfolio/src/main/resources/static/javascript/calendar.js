const PROJECT_ID = window.location.pathname.split('/').slice(-1)




// function getAllEvents() {
//     let httpRequest = new XMLHttpRequest();
//     httpRequest.onreadystatechange = function (){
//         if (httpRequest.readyState === XMLHttpRequest.DONE) {
//             let responseList = []
//             const jsonResponseList = JSON.parse(httpRequest.response)
//             jsonResponseList.forEach((sprint, i) => {
//                 calendar.addEvent(
//                     {
//                         id: sprint.sprintId,
//                         title: sprint.sprintName,
//                         start: sprint.startDate,
//                         end: sprint.endDate
//                     }
//                 )
//             })
//             calendar.render()
//             console.log(responseList)
//         } else {
//             console.log(httpRequest.response)
//         }
//     }
//
//     httpRequest.open('GET', '/project/' + PROJECT_ID + '/getAllSprints/')
//     httpRequest.send()
// }

document.addEventListener('DOMContentLoaded', function() {
    let httpRequest = new XMLHttpRequest();
    let responseList = []
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            const jsonResponseList = JSON.parse(httpRequest.response)
            jsonResponseList.forEach((sprint, i) => {
                responseList.push(
                    {
                        id: sprint.sprintId,
                        title: sprint.sprintName,
                        start: sprint.startDate,
                        end: sprint.endDate
                    }
                )
            })

        } else {
            console.log(httpRequest.response)
        }
    }

    httpRequest.open('GET', '/project/' + PROJECT_ID + '/getAllSprints/')
    httpRequest.send()


    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        events: responseList
    });
    console.log(responseList)
    calendar.render();
});

