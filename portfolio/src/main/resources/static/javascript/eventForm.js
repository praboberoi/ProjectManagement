// Regular expression for Project Name field. No leading white spaces or empty field.
const eventNameRegex = /^\S/
const eventNameSpacesRegex = /^[A-Za-z0-9]+(?: +[A-Za-z0-9]+)*$/

const projectId = document.getElementById("projectId").value;
const startDateElement = document.getElementById('startDate');
const endDateElement = document.getElementById('endDate');
const dateError = document.getElementById('dateError');
const startDateError = document.getElementById('startDateError');
const endDateError = document.getElementById('endDateError');
const projectStartDate = new Date(document.getElementById("projectStartDate").value);
const projectEndDate = new Date(document.getElementById("projectEndDate").value);
const DATE_OPTIONS = { year: 'numeric', month: 'short', day: 'numeric' };

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
        eventName.setCustomValidity("Invalid field.");
    } else {
        eventName.classList.remove("formError");
        eventNameError.innerText = null;
        eventName.setCustomValidity("");
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

    if (startDate > endDate ) {
        console.log("error with dates")
        startDateError.innerText = "Start date must be before the end date.";
        endDateError.innerText = "End date must be after the start date";
        startDateElement.classList.add("formError");
        endDateElement.classList.add("formError");
        return;
    }

    // if (
    //     startDateError.innerText == "" &&
    //     endDateError.innerText == "" ) {
    //     verifyOverlap(startDate, endDate);
    // }
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
        startDateElement.classList.add("formError");
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
