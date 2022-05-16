/**
 * Script for setting the minimum and maximum values for start and end dates of the selected project.
 * Check and display errors in project name, min and max dates.
 */

// Regular expression for Project Name field. No leading white spaces or empty field.
const projectNameRegex = /^\S/

const startDateElement = document.querySelector('#startDate');
const endDateElement = document.querySelector('#endDate');
const startDateError = document.getElementById('startDateError');
const endDateError = document.getElementById('endDateError');
const projectId = document.getElementById('projectId').value;

/**
 * Checks the start date and end date of the project and displays an error if it is invalid
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
 * Checks that the start date of the project is valid
 */
function checkStartDate() {
    const startDate = new Date(startDateElement.value);
    
    let tenYearsFromNow = new Date();
    tenYearsFromNow.setMilliseconds(0);
    tenYearsFromNow.setSeconds(0);
    tenYearsFromNow.setMinutes(0);
    tenYearsFromNow.setHours(12);
    tenYearsFromNow.setFullYear(tenYearsFromNow.getFullYear() + 10);

    let oneYearAgo = new Date();
    oneYearAgo.setMilliseconds(0);
    oneYearAgo.setSeconds(0);
    oneYearAgo.setMinutes(0);
    oneYearAgo.setHours(12);
    oneYearAgo.setFullYear(oneYearAgo.getFullYear() - 1);

    if (startDate > tenYearsFromNow) {
        startDateError.innerText = "Project must start in the next 10 years.";
        startDateElement.classList.add("formError")
        return;
    }
    
    if (startDate < oneYearAgo) {
        startDateError.innerText = "Project must have started in the last year.";
        startDateElement.classList.add("formError");
        return;
    }
    
    startDateError.innerText = "";
    startDateElement.classList.remove("formError")

}

/**
 * Checks that the end date of the project is valid
 */
function checkEndDate() {
    const endDate = new Date(endDateElement.value);
    
    var tenYearsFromNow = new Date();
    tenYearsFromNow.setMilliseconds(0);
    tenYearsFromNow.setSeconds(0);
    tenYearsFromNow.setMinutes(0);
    tenYearsFromNow.setHours(12);
    tenYearsFromNow.setFullYear(tenYearsFromNow.getFullYear() + 10);

    var oneYearAgo = new Date();
    oneYearAgo.setMilliseconds(0);
    oneYearAgo.setSeconds(0);
    oneYearAgo.setMinutes(0);
    oneYearAgo.setHours(12);
    oneYearAgo.setFullYear(oneYearAgo.getFullYear() - 1);

    if (endDate < oneYearAgo) {
        endDateError.innerText = "Project must have started in the last year.";
        endDateElement.classList.add("formError");
        return;
    }

    if (endDate > tenYearsFromNow) {
        endDateError.innerText = "Project must end in the next 10 years.";
        endDateElement.classList.add("formError")
        return;
    }

    endDateError.innerText = "";
    endDateElement.classList.remove("formError")
}

/**
 * Function for error validation of Project Name field.
 * Display error message if input is invalid.
 */
function checkProjectName() {
    let projectName = document.getElementById('project-name');
    let projectNameError = document.getElementById('projectNameError');
    if (projectName.value.length < 1) {
        projectName.classList.add("formError");
        projectNameError.innerText = "Project Name must not be empty";
    } else if (! projectNameRegex.test(projectName.value)) {
        projectName.classList.add("formError");
        projectNameError.innerText = "Project Name must not start with space characters";
    } else {
        projectName.classList.remove("formError");
        projectNameError.innerText = null;
    }
}

/**
 * Calls the server to test for sprints falling outside of the project
 */
 function verifyOverlap(startDate, endDate) {
    httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                if (httpRequest.response != "") {
                    if (httpRequest.response.includes("ends")) {
                        endDateError.innerText = httpRequest.response;
                    } else {
                        startDateError.innerText = httpRequest.response;
                    }
                    startDateElement.classList.add("formError");
                    endDateElement.classList.add("formError");
                }
            }
        }
    }

    httpRequest.open('POST', '/verifyProject/' + projectId, true);
    httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    httpRequest.send("startDate=" + startDate + "&endDate=" + endDate);
}