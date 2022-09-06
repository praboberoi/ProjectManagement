const eventStartDateElement = document.getElementById('eventStartDate');
const eventEndDateElement = document.getElementById('eventEndDate');
const startDateError = document.getElementById('eventStartDateError');
const endDateError = document.getElementById('eventEndDateError');
let currentEventId;

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
    const startDate = eventStartDateElement.value;
    const endDate = eventEndDateElement.value;
    eventStartDateElement.setCustomValidity("");
    eventEndDateElement.setCustomValidity("");

    checkStartDate();

    checkEndDate();

    if(eventStartDateElement.classList.contains("eventFormError") || eventEndDateElement.classList.contains("eventFormError")) {
        document.getElementById("eventFormSubmitButton").disabled = true;
        return;
    } else {
        document.getElementById("eventFormSubmitButton").disabled = false;
    }

    if (startDate >= endDate ) {
        startDateError.innerText = "Start Date must be before the End Date";
        endDateError.innerText = "End Date must be after the Start Date";
        eventStartDateElement.classList.add("eventFormError");
        eventEndDateElement.classList.add("eventFormError");
        document.getElementById("eventFormSubmitButton").disabled = true;
        return;
    }

}

/**
 * Checks that the start date of the event is within the project
 */
function checkStartDate() {
    const startDate = new Date(eventStartDateElement.value);
    if (startDate < new Date(projectStartDate + 'T00:00')) {
        startDateError.innerText = "Event must start on or after the " + (new Date(projectStartDate)).toLocaleDateString('en-NZ', DATE_OPTIONS);
        eventStartDateElement.classList.add("eventFormError");
        return;
    } else if (startDate > new Date(projectEndDate + 'T00:00')) {
        startDateError.innerText = "Event must start before the project ends";
        eventStartDateElement.classList.add("eventFormError")
        return;
    }

    startDateError.innerText = "";
    eventStartDateElement.classList.remove("eventFormError")
}

/**
 * Checks that the end date of the event is within the project
 */
function checkEndDate() {
    const endDate = new Date(eventEndDateElement.value);

    if (endDate < new Date(projectStartDate + 'T00:00')) {
        endDateError.innerText = "Event must start on or before the " + (new Date(projectStartDate)).toLocaleDateString('en-NZ', DATE_OPTIONS);
        eventEndDateElement.classList.add("eventFormError");
        return;
    } else if (endDate > new Date(projectEndDate + 'T00:00')) {
        endDateError.innerText = "Event must end on or before the " + (new Date(projectEndDate)).toLocaleDateString('en-NZ', DATE_OPTIONS);
        eventEndDateElement.classList.add("eventFormError");
        return;
    }

    endDateError.innerText = "";
    eventEndDateElement.classList.remove("eventFormError")
}

/**
 * Populate event creation or editing modal with proper values
 * @param isEdit Bool for if create or edit selected
 * @param eventId ID of event
 * @param eventName Name of event
 * @param startDate Event start date and time
 * @param endDate Event end date and time
 */
function populateEventModal(isEdit, eventId, eventName, startDate, endDate) {
    currentEventId = eventId
    if (isEdit) {
        document.getElementById('eventFormTitle').innerText =  'Edit Event: ' + eventName;
        document.getElementById('eventFormSubmitButton').innerText =  'Save';
        document.getElementById('eventStartDate').value = startDate.replace(" ", "T");
        document.getElementById('eventEndDate').value =  endDate.replace(" ", "T");
    } else {
        document.getElementById('eventFormTitle').innerText =  'Create New Event';
        document.getElementById('eventFormSubmitButton').innerText =  'Create';
        document.getElementById('eventStartDate').value =  startDate + 'T00:00';
        document.getElementById('eventEndDate').value =  endDate + 'T00:00';
    }
    document.getElementById('eventId').value =  eventId;
    document.getElementById('event-name').value =  eventName;
    checkEventName()
    checkEventDates()
}

/**
 * Sends a notification to the server when a user starts editing an event
 */
document.getElementById('eventFormModal').addEventListener('shown.bs.modal', function () {
    stompClient.publish({destination: "/app/event/edit", body: JSON.stringify({'active': true, 'projectId': projectId, 'eventId': currentEventId})})
});

/**
 * Sends a notification to the server when a user stops editing an event
 */
document.getElementById('eventFormModal').addEventListener('hidden.bs.modal', function () {
    stompClient.publish({destination: "/app/event/edit", body: JSON.stringify({'active': false, 'projectId': projectId, 'eventId': currentEventId})})
});