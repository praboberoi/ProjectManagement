/**
 * Script for setting the minimum and maximum values for start and end dates of the selected project.
 * Check and display errors in project name, min and max dates.
 */

// Regular expression for Project Name field. No leading white spaces or empty field.
const projectNameRegex = /^\S/

/**
 * Checks the start date and end date of the project and displays an error if it is invalid
 */
function checkProjectDates() {
    const startDateElement = document.querySelector('#projectFormStartDate');
    const endDateElement = document.querySelector('#projectFormEndDate');
    const startDateError = document.getElementById('startDateError');
    const endDateError = document.getElementById('endDateError');
    const startDate = startDateElement.value;
    const endDate = endDateElement.value;

    startDateElement.setCustomValidity("");
    endDateElement.setCustomValidity("");

    checkStartDate();
    checkEndDate();

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
}

/**
 * Checks that the start date of the project is valid
 */
function checkStartDate() {
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
    const endDateElement = document.querySelector('#projectFormEndDate');
    const endDateError = document.getElementById('endDateError');
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
    if (projectName.value.length < 1 || projectName.value.length > 32) {
        projectName.classList.add("formError");
        projectNameError.innerText = "Project Name must not be empty or greater than 32 characters";
    } else if (!projectNameRegex.test(projectName.value)) {
        projectName.classList.add("formError");
        projectNameError.innerText = "Project Name must not start with space characters";
    } else {
        projectName.classList.remove("formError");
        projectNameError.innerText = null;
    }
}

/**
 * Updates the characters remaining in the description.
 */
function checkProjectDescription() {
    let descriptionElement = document.getElementById("projectFormDescription");
    let descErrorElement = document.getElementById("descriptionError");

    let charMessage = document.getElementById("charCount");
    let charCount = descriptionElement.value.length;
    charMessage.innerText = charCount + ' '

    if (descriptionElement.value.length > 250) {
        descErrorElement.classList.add("formError");
        descErrorElement.innerText = "Description must be less than 250 characters."
    } else {
        descErrorElement.classList.remove("formError");
        descErrorElement.innerText = null;
    }

}

/**
 * Updates the characters remaining in the project name.
 */
function checkProjectName() {
    let nameElement = document.getElementById("project-name");
    let nameErrorElement = document.getElementById("projectNameError");

    let charMessage = document.getElementById("projectCharCount");
    let charCount = nameElement.value.length;
    charMessage.innerText = charCount + ' '

    if (nameElement.value.length > 250) {
        nameErrorElement.classList.add("formError");
        nameErrorElement.innerText = "Description must be less than 250 characters."
    } else {
        nameErrorElement.classList.remove("formError");
        nameErrorElement.innerText = null;
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

    httpRequest = new XMLHttpRequest();

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
    httpRequest.send("startDate=" + startDate + "&endDate=" + endDate);
}

/**
 * Initilises the project modal for editing the selected project
 * @param {int} projectId 
 */
function editProjectModalInit(projectId) {
    let projectName = document.getElementById(`project${projectId}-card`).getElementsByClassName('project-name')[0].innerText
    
    document.getElementById('projectFormTitle').innerText = "Edit " + projectName
    
    document.getElementById('projectId').value = projectId
    
    document.getElementById('project-name').value = projectName;
    checkProjectName()

    let projectStartDate = document.getElementById(`project${projectId}-startDate`).value
    document.getElementById('projectFormStartDate').value = projectStartDate;

    let projectEndDate = document.getElementById(`project${projectId}-endDate`).value
    document.getElementById('projectFormEndDate').value = projectEndDate;
    checkProjectDates()

    let projectDescription = document.getElementById(`project${projectId}-description`).innerText
    document.getElementById('projectFormDescription').value = projectDescription;
    checkProjectDescription()

    document.getElementById('projectFormCreateBtn').hidden = true
    document.getElementById('projectFormEditBtn').hidden = false
}

/**
 * Initilises the project modal for editing the selected project
 */
function createProjectModalInit() {
    document.getElementById('projectFormTitle').innerText = "Create New Project"
    
    document.getElementById('projectId').value = 0
    
    document.getElementById('project-name').value = "";
    checkProjectName()

    document.getElementById('projectFormStartDate').value = new Date().toLocaleDateString("en-CA");

    let endDate = new Date();
    endDate.setMonth(endDate.getMonth() + 8);

    document.getElementById('projectFormEndDate').value = endDate.toLocaleDateString("en-CA");
    checkProjectDates()

    document.getElementById('projectFormDescription').value = "";
    checkProjectDescription()

    document.getElementById('projectFormCreateBtn').hidden = false
    document.getElementById('projectFormEditBtn').hidden = true
}

/**
 * Send a request to the server to save the newly created project
 */
function saveProject() {
    let modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('projectFormModal'))
    let modalError = document.getElementById('projectFormModalError')
    if (validateForm()) {
        let httpRequest = new XMLHttpRequest();

        httpRequest.onreadystatechange = () => updateModal(httpRequest, modal, modalError)

        httpRequest.open('POST', apiPrefix + `/project`);

        let formData = new FormData(document.forms.createProjectForm)

        httpRequest.send(formData);
    }
}