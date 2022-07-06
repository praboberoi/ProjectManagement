/**
 * Script for setting the minimum and maximum values for start and end dates of the selected sprint.
 */

// Regular expression for Sprint Name field. No leading white spaces or empty field.
const sprintNameRegex = /^\S/;
const projectId = document.getElementById("projectId").value;
const startDateElement = document.getElementById('startDate');
const endDateElement = document.getElementById('endDate');
const labelElement = document.getElementById('sprint-label');
const dateError = document.getElementById('dateError');
const startDateError = document.getElementById('startDateError');
const endDateError = document.getElementById('endDateError');
const projectStartDate = new Date(document.getElementById("projectStartDate").value);
const projectEndDate = new Date(document.getElementById("projectEndDate").value);
const DATE_OPTIONS = { year: 'numeric', month: 'short', day: 'numeric' };

/**
 * Function for error validation of Sprint Name field.
 * Display error message if input is invalid.
  */
function checkSprintName() {
    let sprintName = document.getElementById('sprint-name');
    let sprintNameError = document.getElementById('sprintNameError');
    if (sprintName.value.length < 1) {
        sprintName.classList.add("formError");
        sprintNameError.innerText = "Sprint Name must not be empty";
    } else if (! sprintNameRegex.test(sprintName.value)) {
        sprintName.classList.add("formError");
        sprintNameError.innerText = "Sprint Name must not start with empty space";
    } else {
        sprintName.classList.remove("formError");
        sprintNameError.innerText = null;
    }
}

/**
 * Checks that the start and end dates of the sprint are valid
 */
function checkDates() {
    const startDate = startDateElement.value;
    const endDate = endDateElement.value;
    startDateElement.setCustomValidity("");
    endDateElement.setCustomValidity("");

    checkStartDate();
    checkEndDate();

    if (startDate > endDate ) {
        startDateError.innerText = "Start date must be before the end date.";
        endDateError.innerText = "End date must be after the start date";
        startDateElement.classList.add("formError");
        endDateElement.classList.add("formError");
        return;
    }

    if (
    startDateError.innerText == "" &&
    endDateError.innerText == "" ) {
        verifyOverlap(startDate, endDate);
    }
}

/**
 * Checks that the start date of the sprint is within the project
 */
function checkStartDate() {
    const startDate = new Date(startDateElement.value);

    if (startDate < projectStartDate) {
        startDateError.innerText = "Sprint must start after " + projectStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        startDateElement.classList.add("formError");
        return;
    } else if (startDate > projectEndDate) {
        startDateError.innerText = "Sprint must start before the project ends";
        startDateElement.classList.add("formError");
        return;
    }

    startDateError.innerText = "";
    startDateElement.classList.remove("formError")
}

/**
 * Checks that the end date of the sprint is within the project
 */
function checkEndDate() {
    const endDate = new Date(endDateElement.value);

    if (endDate < projectStartDate) {
        endDateError.innerText = "Sprint must start after " + projectStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        endDateElement.classList.add("formError");
        return;
    } else if (endDate > projectEndDate) {
        endDateError.innerText = "Sprint must end before " + projectEndDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        endDateElement.classList.add("formError");
        return;
    }

    endDateError.innerText = "";
    endDateElement.classList.remove("formError")
}

/**
 * Calls the server to test sprint for overlap
 */
function verifyOverlap(startDate, endDate) {
    httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                if (httpRequest.response != "") {
                    startDateError.innerText = httpRequest.response;
                    startDateElement.classList.add("formError");
                    endDateElement.classList.add("formError");
                }
            }
        }
    }

    httpRequest.open('POST', '/project/' + projectId + '/verifySprint', true);
    httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    httpRequest.send("startDate=" + startDate + "&endDate=" + endDate + "&label=" + labelElement.value);
}