// Regular expression for Sprint Name field. No leading white spaces or empty field.
const eventNameRegex = /^\S/
const eventNameSpacesRegex = /^[A-Za-z0-9]+(?: +[A-Za-z0-9]+)*$/

const projectId = document.getElementById("projectId").value;
const startDateElement = document.getElementById('startDate');
const endDateElement = document.getElementById('endDate');
const startDateError = document.getElementById('startDateError');
const endDateError = document.getElementById('endDateError');
const projectStartDate = new Date(document.getElementById("projectStartDate").value);
const projectEndDate = new Date(document.getElementById("projectEndDate").value);
const DATE_OPTIONS = { year: 'numeric', month: 'short', day: 'numeric' };

const startTimeElement = document.querySelector('#startTime');
const endTimeElement = document.querySelector('#endTime');

const startTimeError = document.getElementById('startTimeError');
const endTimeError = document.getElementById('endTimeError');

/**
 * Function for error validation of Event Name field.
 * Display error message if input is invalid.
 */
function checkEventName() {
    let eventName = document.getElementById('event-name');
    let eventNameError = document.getElementById('eventNameError');
    if (eventName.value.length < 1) {
        eventName.classList.add("formError");
        eventNameError.innerText = "Event Name must not be empty";
    } else if (! eventNameSpacesRegex.test(eventName.value)) {
        eventName.classList.add("formError");
        eventNameError.innerText = "Event name must not start or end with space characters";
    } else {
        eventName.classList.remove("formError");
        eventNameError.innerText = null;
    }
}


/**
 * Checks that the start and end dates of the event are valid
 */
function checkEventDates() {
    const startDate = startDateElement.value;
    const endDate = endDateElement.value;
    startDateElement.setCustomValidity("");
    endDateElement.setCustomValidity("");

    checkStartDate();
    checkEndDate();
    checkEventTimes()
    if (startDate > endDate ) {
        startDateError.innerText = "Start Date must be on or before the End Date.";
        endDateError.innerText = "End Date must be on or after the Start Date";
        startDateElement.classList.add("formError");
        endDateElement.classList.add("formError");
        return;
    }

}

/**
 * Checks that the start date of the event is within the project
 */
function checkStartDate() {
    const startDate = new Date(startDateElement.value);

    if (startDate < projectStartDate) {
        startDateError.innerText = "Event must start after " + projectStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        startDateElement.classList.add("formError");
        return;
    } else if (startDate > projectEndDate) {
        startDateError.innerText = "Event must start before the project ends";
        startDateElement.classList.add("formError")
        return;
    }

    startDateError.innerText = "";
    startDateElement.classList.remove("formError")
}

/**
 * Checks that the end date of the event is within the project
 */
function checkEndDate() {
    const endDate = new Date(endDateElement.value);

    if (endDate < projectStartDate) {
        endDateError.innerText = "Event must start after " + projectStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        endDateElement.classList.add("formError");
        return;
    } else if (endDate > projectEndDate) {
        endDateError.innerText = "Event must end before " + projectEndDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        endDateElement.classList.add("formError");
        return;
    }

    endDateError.innerText = "";
    endDateElement.classList.remove("formError")
}


/**
 * Checks that the start and end times of the event are valid
 */
function checkEventTimes() {
    const startTime = startTimeElement.valueAsNumber;
    const endTime = endTimeElement.valueAsNumber;

    const startDate = new Date(startDateElement.value);
    const endDate = new Date(endDateElement.value);
    if ( (startDate.getTime() == endDate.getTime()) && (endTime <= startTime)) {
        startTimeError.innerText = "Start Time must be before the End Time." ;
        endTimeError.innerText = "End Time must be after the Start Time";
        startTimeElement.classList.add("formError");
        endTimeElement.classList.add("formError");
        return;
    }
        startTimeElement.classList.remove("formError");
        startTimeError.innerText = null;
        endTimeElement.classList.remove("formError");
        endTimeError.innerText = null;
}