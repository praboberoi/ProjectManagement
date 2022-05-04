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

function checkDates() {
    const startDate = startDateElement.value;
    const endDate = endDateElement.value;

    if (startDate > endDate ) {
        dateError.innerText = "Start date must be before the end date.";
        startDateElement.classList.add("form_error");
        endDateElement.classList.add("form_error");
        return;
    } else {
        dateError.innerText = "";
    }

    checkStartDate();
    checkEndDate();

    if (dateError.innerText == "" &&
    startDateError.innerText == "" &&
    endDateError.innerText == "" ) {
        verifyOverlap(startDate, endDate);
    }
}

function checkStartDate() {
    const startDate = new Date(startDateElement.value);
    
    startDateError.innerText = "";
    startDateElement.classList.remove("form_error")
}

function checkEndDate() {
    const endDate = new Date(endDateElement.value);
    
    endDateError.innerText = "";
    endDateElement.classList.remove("form_error")
}

function verifyOverlap(startDate, endDate) {
    httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                console.log(httpRequest);
                if (httpRequest.response == "false") {
                    dateError.innerText = "Sprint cannot occur during another sprint.";
                    startDateElement.classList.add("form_error");
                    endDateElement.classList.add("form_error");
                }
            } else {
                console.log(httpRequest);
            }
        }
    }

    httpRequest.open('POST', '/project/' + projectId + '/verifySprint', true);
    httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    httpRequest.send("startDate=" + startDate + "&endDate=" + endDate);
}