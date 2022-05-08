/**
 * Script for setting the minimum and maximum values for start and end dates of the selected sprint.
 */

// Regular expression for Sprint Name field. No leading white spaces or empty field.
const sprintNameRegex = /^\S/;
const projectId = document.getElementById("projectId").value;
const startDateElement = document.getElementById('startDate');
const endDateElement = document.getElementById('endDate');
const dateError = document.getElementById('dateError');
const startDateError = document.getElementById('startDateError');
const endDateError = document.getElementById('endDateError');
const projectStartDate = document.getElementById("project-start-date").value;
const projectEndDate = document.getElementById("project-end-date").value;

/**
 * Function for error validation of Sprint Name field.
 * Display error message if input is invalid.
  */
function checkSprintName() {
    let sprintName = document.getElementById('sprint-name');
    let sprintNameError = document.getElementById('sprintNameError');
    if (sprintName.value.length < 1) {
        sprintName.classList.add("form_error");
        sprintNameError.innerText = "Sprint Name must not be empty";
    } else if (! sprintNameRegex.test(sprintName.value)) {
        sprintName.classList.add("form_error");
        sprintNameError.innerText = "Sprint Name must not start with empty space";
    } else {
        sprintName.classList.remove("form_error");
        sprintNameError.innerText = null;
    }
}

/**
 * Checks that the start and end dates of the sprint are valid
 */
function checkDates() {
    const startDate = startDateElement.value;
    const endDate = endDateElement.value;

    checkStartDate();
    checkEndDate();

    if (startDate > endDate ) {
        dateError.innerText = "Start date must be before the end date.";
        startDateElement.classList.add("form_error");
        endDateElement.classList.add("form_error");
        return;
    } else {
        dateError.innerText = "";
    }


    if (dateError.innerText == "" &&
    startDateError.innerText == "" &&
    endDateError.innerText == "" ) {
        verifyOverlap(startDate, endDate);
    }
}

/**
 * Checks that the start date of the project is valid
 */
function checkStartDate() {
    const startDate = startDateElement.value;

    if (startDate < projectStartDate) {
        startDateError.innerText = "Project must start after " + projectStartDate;
        startDateElement.classList.add("form_error");
        return;
    } else if (startDate > projectEndDate) {
        startDateError.innerText = "Project must finish before " + projectEndDate;
        startDateElement.classList.add("form_error");
        return;
    }
    
    startDateError.innerText = "";
    startDateElement.classList.remove("form_error")
}

/**
 * Checks that the end date of the project is valid
 */
function checkEndDate() {
    const endDate = new Date(endDateElement.value);

    if (endDate < projectStartDate) {
        startDateError.innerText = "Project must start after " + projectStartDate;
        startDateElement.classList.add("form_error");
        return;
    } else if (endDate > projectEndDate) {
        endDateError.innerText = "Project must finish before " + projectEndDate;
        endDateElement.classList.add("form_error");
        return;
    }
    
    endDateError.innerText = "";
    endDateElement.classList.remove("form_error")
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
                    dateError.innerText = httpRequest.response;
                    startDateElement.classList.add("form_error");
                    endDateElement.classList.add("form_error");
                }
            }
        }
    }

    httpRequest.open('POST', '/project/' + projectId + '/verifySprint', true);
    httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    httpRequest.send("startDate=" + startDate + "&endDate=" + endDate);
}