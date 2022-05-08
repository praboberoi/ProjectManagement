/**
 * Script for setting the minimum and maximum values for start and end dates of the selected project.
 * Check and display errors in project name, min and max dates.
 */

// Regular expression for Project Name field. No leading white spaces or empty field.
const projectNameRegex = /^\S/

const startDateElement = document.querySelector('#startDate');
const endDateElement = document.querySelector('#endDate');
const dateError = document.getElementById('dateError');
const startDateError = document.getElementById('startDateError');
const endDateError = document.getElementById('endDateError');

/**
 * Checks the start date and end date of the project and displays an error if it is invalid
 */
function checkDates() {
    const startDate = startDateElement.value;
    const endDate = endDateElement.value;

    checkStartDate();
    checkEndDate();

    if (startDate > endDate ) {
        dateError.innerText = "Start date must be before the end date.";
        startDateElement.classList.add("formError");
        endDateElement.classList.add("formError");
    } else {
        dateError.innerText = "";
    }

}

/**
 * Checks that the start date of the project is valid
 */
function checkStartDate() {
    const startDate = new Date(startDateElement.value);
    
    let tenYearsFromNow = new Date();
    tenYearsFromNow.setFullYear(tenYearsFromNow.getFullYear() + 10);

    let oneYearAgo = new Date();
    oneYearAgo.setFullYear(oneYearAgo.getFullYear() - 1);

    if (startDate > tenYearsFromNow) {
        startDateError.innerText = "Project must end in the next 10 years.";
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
    tenYearsFromNow.setFullYear(tenYearsFromNow.getFullYear() + 10);

    var oneYearAgo = new Date();
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
    } else if (projectNameRegex.test(projectName.value)) {
        projectName.classList.add("formError");
        projectNameError.innerText = "Project Name must not start with space characters";
    } else {
        projectName.classList.remove("formError");
        projectNameError.innerText = null;
    }
}

