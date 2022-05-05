
async function getAllEvents() {

}

document.addEventListener('DOMContentLoaded', function() {
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        events: [
            {
                id: 'a',
                title: 'my event',
                start: '2022-05-01'
            }
        ]
    });
    calendar.render();
});