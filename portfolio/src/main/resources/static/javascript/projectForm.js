/**
 * Script for setting the minimum and maximum values for start and end dates of the selected project.
 * Check and display errors in project name, min and max dates.
 */

// Regular expression for Project Name field. No leading white spaces or empty field.
const projectNameRegex = /^\S/
const emojiRegx = /\p{Extended_Pictographic}/u

let projectFormUpdate = false

/**
 * Checks the start date and end date of the project and displays an error if it is invalid
 */
function checkProjectDates() {
    if (!projectFormUpdate) {
        updateFormButton()
        return
    }
    const startDateElement = document.querySelector('#projectFormStartDate');
    const endDateElement = document.querySelector('#projectFormEndDate');
    const startDateError = document.getElementById('startDateError');
    const endDateError = document.getElementById('endDateError');
    const startDate = startDateElement.value;
    const endDate = endDateElement.value;

    startDateElement.setCustomValidity("");
    endDateElement.setCustomValidity("");

    let valid = true

    valid = valid && checkProjectStartDate();
    valid = valid && checkProjectEndDate();

    if (startDate > endDate) {
        startDateError.innerText = "Start date must be before the end date.";
        endDateError.innerText = "End date must be after the start date";
        startDateElement.classList.add("formError");
        endDateElement.classList.add("formError");
        return;
    }

    if (
        startDateError.innerText == "" &&
        endDateError.innerText == "") {
        verifyOverlap(startDate, endDate);
    }
    return valid
}

/**
 * Checks that the start date of the project is valid
 */
function checkProjectStartDate() {
    const startDateElement = document.querySelector('#projectFormStartDate');
    const startDateError = document.getElementById('startDateError');
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
        return false;
    }

    if (startDate < oneYearAgo) {
        startDateError.innerText = "Project must have started in the last year.";
        startDateElement.classList.add("formError");
        return false;
    }

    startDateError.innerText = "";
    startDateElement.classList.remove("formError")
    return true;
}

/**
 * Checks that the end date of the project is valid
 */
function checkProjectEndDate() {
    const endDateElement = document.querySelector('#projectFormEndDate');
    const endDateError = document.getElementById('endDateError');
    const endDate = new Date(endDateElement.value);

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

    if (endDate < oneYearAgo) {
        endDateError.innerText = "Project must have started in the last year.";
        endDateElement.classList.add("formError");
        return false;
    }

    if (endDate > tenYearsFromNow) {
        endDateError.innerText = "Project must end in the next 10 years.";
        endDateElement.classList.add("formError")
        return false;
    }

    endDateError.innerText = "";
    endDateElement.classList.remove("formError")
    return true
}

/**
 * Updates the characters remaining in the description.
 */
function checkProjectDescription() {
    if (!projectFormUpdate) {
        updateFormButton()
        return
    }
    let descriptionElement = document.getElementById("projectFormDescription");
    let descErrorElement = document.getElementById("descriptionError");

    let charMessage = document.getElementById("charCount");
    let charCount = descriptionElement.value.length;
    charMessage.innerText = charCount + ' '

    if (descriptionElement.value.length > 250) {
        descErrorElement.classList.add("formError");
        descErrorElement.innerText = "Description must be less than 250 characters."
        return false;
    } else {
        descErrorElement.classList.remove("formError");
        descErrorElement.innerText = null;
        return true;
    }

}

/**
 * Function for error validation of Project Name field.
 * Display error message if input is invalid.
 */
 function checkProjectName() {
    if (!projectFormUpdate) {
        updateFormButton()
        return
    }

    let projectName = document.getElementById('project-name');
    let projectNameError = document.getElementById('projectNameError');
    let charMessage = document.getElementById("projectCharCount");

    let charCount = projectName.value.length;
    charMessage.innerText = charCount + ' '

    if (projectName.value.length < 1 || projectName.value.length > 50) {
        projectName.classList.add("formError");
        projectNameError.innerText = "Project name must not be empty or greater than 32 characters";
    } else if (! projectNameRegex.test(projectName.value)) {
        projectName.classList.add("formError");
        projectNameError.innerText = "Project name must not start with space characters";
    } else if (emojiRegx.test(projectName.value)) {
        projectName.classList.add("formError");
        projectNameError.innerText = "Project name must not contain an emoji";
    } else {
        projectName.classList.remove("formError");
        projectNameError.innerText = null;
    }
}

/**
 * Updates the characters remaining in the description.
 */
function checkProjectDescription () {
    let descriptionElement = document.getElementById("projectDescription");
    let descErrorElement = document.getElementById("descriptionError");

    let charMessage = document.getElementById("charCount");
    let charCount = descriptionElement.value.length;
    charMessage.innerText = charCount + ' '

    if (descriptionElement.value.length > 250)
    {
        descErrorElement.classList.add("formError");
        descErrorElement.innerText = "Description must be less than 250 characters."

    } else if ( emojiRegx.test(descriptionElement.value)) {
        descErrorElement.classList.add("formError");
        descErrorElement.innerText = "Project description must not contain an emoji";

    } else {
        descErrorElement.classList.remove("formError");
        descErrorElement.innerText = null;
    }

}


/**
 * Calls the server to test for sprints falling outside of the project
 */
function verifyOverlap(startDate, endDate) {
    const startDateElement = document.querySelector('#projectFormStartDate');
    const endDateElement = document.querySelector('#projectFormEndDate');
    const startDateError = document.getElementById('startDateError');
    const endDateError = document.getElementById('endDateError');
    const projectId = document.getElementById('projectId').value;

    let httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function () {
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
    httpRequest.send("startDate=" + startDate + "&endDate=" + endDate +  "&projectName=" + projectName + "&projectDescription=" + description);
}
