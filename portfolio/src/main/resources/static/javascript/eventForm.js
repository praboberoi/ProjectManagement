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

const deadlineDateElement = document.getElementById('date');
const deadlineDateError = document.getElementById('deadlineDateError');
/**
 * Function for error validation of Event Name field.
 * Display error message if input is invalid.
 */
function checkEventName() {
    const eventName = document.getElementById('event-name');
    const eventNameError = document.getElementById('eventNameError');
    let charMessage = document.getElementById("charCount");
    let charCount = eventName.value.length;
    charMessage.innerText = charCount + ' '

    if (eventName.value.length < 1) {
        eventName.classList.add("formError");
        eventNameError.innerText = "Event Name must not be empty";
    } else if (eventName.value.length > 50){
        eventName.classList.add("formError");
        eventNameError.innerText = "Event Name cannot exceed 50 characters";
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

    if(startDateElement.classList.contains("formError") || endDateElement.classList.contains("formError")) {
        document.getElementById("formSubmitButton").disabled = true;
        return;
    } else {
        document.getElementById("formSubmitButton").disabled = false;
    }

    if (startDate >= endDate ) {
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
 * Checks that the date of the deadline is valid
 */
// function checkDeadlineDates() {
//     const date = deadlineDateElement.value;
//     console.log("adskgfj")
//     console.log(date)
//     console.log(projectStartDate)
//
//     if(date < projectStartDate) {
//         console.log("less")
//     } else{
//         console.log("more")
//         deadlineDateError.innerText = "Deadline must start after " + projectStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
//         deadlineDateElement.classList.add("formError");
//
//     }
//
//     // const date = new Date(deadlineDateElement.value);
//     // date.setCustomValidity("")
//
//     // if(deadlineDateElement.classList.contains("formError")) {
//     //     document.getElementById("formSubmitButton").disabled = true;
//     //     return;
//     // } else {
//     //     document.getElementById("formSubmitButton").disabled = false;
//     // }
//     deadlineDateError.innerText="asjfd;adsjf"
//     if (date < projectStartDate) {
//         deadlineDateError.innerText = "Deadline must start after " + projectStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
//         deadlineDateElement.classList.add("formError");
//         return;
//     } else if (date > projectEndDate) {
//         deadlineDateError.innerText = "Deadline must end before " + projectEndDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
//         deadlineDateElement.classList.add("formError");
//         return;
//     }
//
//     deadlineDateError.innerText = "";
//     deadlineDateElement.classList.remove("formError")
//
// }


/**
 * Function for error validation of Deadline Name field.
 * Display error message if input is invalid.
 */
function checkDeadlineName() {
    const deadlineName = document.getElementById('deadline-name');
    const deadlineNameError = document.getElementById('deadlineNameError');
    let charMessage = document.getElementById("charCount");
    let charCount = deadlineName.value.length;
    charMessage.innerText = charCount + ' '

    if (deadlineName.value.length < 1) {
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline Name must not be empty";
    } else if (deadlineName.value.length < 3){
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline name must be at least 3 characters";
    } else if (deadlineName.value.length > 50){
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline Name cannot exceed 50 characters";
    } else {
        deadlineName.classList.remove("formError");
        deadlineNameError.innerText = null;
    }
}

function checkDeadlineDates(){
    const date = document.getElementById('date').value;
    const dateError = document.getElementById('deadlineDateError');

    if (date < projectStartDate) {
        date.classList.add('formError');
        dateError.innerText = "hood"
    }
}

