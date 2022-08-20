const startDateElement = document.getElementById('eventStartDate');
const endDateElement = document.getElementById('eventEndDate');
const startDateError = document.getElementById('eventStartDateError');
const endDateError = document.getElementById('eventEndDateError');

/**
 * Function for error validation of Event Name field.
 * Display error message if input is invalid.
 */
function checkEventName() {
    const eventName = document.getElementById('event-name');
    const eventNameError = document.getElementById('eventNameError');
    let charMessage = document.getElementById("charCount");
    let trimmedEventName = eventName.value.trim()
    let charCount = trimmedEventName.length;
    charMessage.innerText = charCount + ' '
    if (charCount < 1) {
        eventName.classList.add("eventFormError");
        eventNameError.innerText = "Event Name must not be empty";
        document.getElementById("eventFormSubmitButton").disabled = true;
    } else if (charCount > 50){
        eventName.classList.add("eventFormError");
        eventNameError.innerText = "Event Name cannot exceed 50 characters";
        document.getElementById("eventFormSubmitButton").disabled = true;
    } else {
        eventName.classList.remove("eventFormError");
        eventNameError.innerText = null;
        document.getElementById("eventFormSubmitButton").disabled = false;

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

    if(startDateElement.classList.contains("eventFormError") || endDateElement.classList.contains("eventFormError")) {
        document.getElementById("eventFormSubmitButton").disabled = true;
        return;
    } else {
        document.getElementById("eventFormSubmitButton").disabled = false;
    }

    if (startDate >= endDate ) {
        startDateError.innerText = "Start Date must be before the End Date";
        endDateError.innerText = "End Date must be after the Start Date";
        startDateElement.classList.add("eventFormError");
        endDateElement.classList.add("eventFormError");
        document.getElementById("eventFormSubmitButton").disabled = true;
        return;
    }

}

/**
 * Checks that the start date of the event is within the project
 */
function checkStartDate() {
    const startDate = new Date(startDateElement.value);
    if (startDate < new Date(projectStartDate + 'T00:00')) {
        startDateError.innerText = "Event must start on or after the " + (new Date(projectStartDate)).toLocaleDateString('en-NZ', DATE_OPTIONS);
        startDateElement.classList.add("eventFormError");
        return;
    } else if (startDate > new Date(projectEndDate + 'T00:00')) {
        startDateError.innerText = "Event must start before the project ends";
        startDateElement.classList.add("eventFormError")
        return;
    }

    startDateError.innerText = "";
    startDateElement.classList.remove("eventFormError")
}

/**
 * Checks that the end date of the event is within the project
 */
function checkEndDate() {
    const endDate = new Date(endDateElement.value);

    if (endDate < new Date(projectStartDate + 'T00:00')) {
        endDateError.innerText = "Event must start on or before the " + (new Date(projectStartDate)).toLocaleDateString('en-NZ', DATE_OPTIONS);
        endDateElement.classList.add("eventFormError");
        return;
    } else if (endDate > new Date(projectEndDate + 'T00:00')) {
        endDateError.innerText = "Event must end on or before the " + (new Date(projectEndDate)).toLocaleDateString('en-NZ', DATE_OPTIONS);
        endDateElement.classList.add("eventFormError");
        return;
    }

    endDateError.innerText = "";
    endDateElement.classList.remove("eventFormError")
}

function populateEventModal(isEdit, eventId, eventName, startDate, endDate) {
    if (isEdit) {
        document.getElementById('eventFormTitle').innerText =  'Edit Event: ' + eventName;
        document.getElementById('eventStartDate').value =  startDate;
        document.getElementById('eventEndDate').value =  endDate;
    } else {
        document.getElementById('eventFormTitle').innerText =  'Create New Event';
        document.getElementById('eventStartDate').value =  startDate + 'T00:00';
        document.getElementById('eventEndDate').value =  endDate + 'T00:00';
    }
    document.getElementById('eventId').value =  eventId;
    document.getElementById('event-name').value =  eventName;
    checkEventName()
    checkEventDates()
}


