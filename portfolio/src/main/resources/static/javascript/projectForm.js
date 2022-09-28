/**
 * Script for setting the minimum and maximum values for start and end dates of the selected project.
 * Check and display errors in project name, min and max dates.
 */

// Regular expression for Project Name field. No leading white spaces or empty field.
const projectNameRegex = /^\S/

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
        projectNameError.innerText = "Project Name must not be empty or greater than 50 characters";
    } else if (!projectNameRegex.test(projectName.value)) {
        projectName.classList.add("formError");
        projectNameError.innerText = "Project Name must not start with space characters";
    } else {
        projectName.classList.remove("formError");
        projectNameError.innerText = null;
        return true
    }
    return false
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
    httpRequest.send("startDate=" + startDate + "&endDate=" + endDate);
}

/**
 * Initilises the delete modal for deleting a project
 * @param {int} projectId 
 */
 function projectDeleteModalInit(projectId) {
    let projectName = document.getElementById(`project${projectId}-card`).getElementsByClassName('project-name')[0].innerText
    document.getElementById('messageProject').innerText =  `Are you sure you want to delete ${projectName}`;
    document.getElementById('confirmProjectDeleteBtn').setAttribute('onclick', `deleteProject(${projectId})`);
}

/**
 * Initilises the project modal for editing the selected project
 * @param {int} projectId 
 */
function editProjectModalInit(projectId) {
    let projectName = document.getElementById(`project${projectId}-card`).getElementsByClassName('project-name')[0].innerText
    
    document.getElementById('projectFormModalError').innerText = ""
    document.getElementById('projectFormTitle').innerText = "Edit " + projectName
    
    document.getElementById('projectId').value = projectId
    
    document.getElementById('project-name').value = projectName;

    
    let projectStartDate = new Date(document.getElementById(`project${projectId}-startDate`).value)
    document.getElementById('projectFormStartDate').value = new Date(projectStartDate).toLocaleDateString("en-CA");

    
    let projectEndDate = new Date(document.getElementById(`project${projectId}-endDate`).value)
    document.getElementById('projectFormEndDate').value = projectEndDate.toLocaleDateString("en-CA");
    

    let minStartDate = new Date();
    minStartDate.setFullYear(minStartDate.getFullYear() - 1);

    let maxEndDate = new Date();
    maxEndDate.setFullYear(maxEndDate.getFullYear() + 10);
    

    document.getElementById('projectFormStartDate').setAttribute("min", new Date(Math.min(minStartDate, projectStartDate)).toLocaleDateString("en-CA"));
    document.getElementById('projectFormStartDate').setAttribute("max", new Date(Math.max(maxEndDate, projectEndDate)).toLocaleDateString("en-CA"));
    
    document.getElementById('projectFormEndDate').setAttribute("min", new Date(Math.min(minStartDate, projectStartDate)).toLocaleDateString("en-CA"));
    document.getElementById('projectFormEndDate').setAttribute("max", new Date(Math.max(maxEndDate, projectEndDate)).toLocaleDateString("en-CA"));


    let projectDescription = document.getElementById(`project${projectId}-description`).innerText
    document.getElementById('projectFormDescription').value = projectDescription;
    updateFormButton()

    document.getElementById('projectFormCreateBtn').hidden = true
    document.getElementById('projectFormEditBtn').hidden = false
}

/**
 * Initilises the project modal for editing the selected project
 */
function createProjectModalInit() {
    document.getElementById('projectFormModalError').innerText = ""
    document.getElementById('projectFormTitle').innerText = "Create New Project"
    
    document.getElementById('projectId').value = 0
    
    document.getElementById('project-name').value = "Project " + new Date().getFullYear();

    let minStartDate = new Date();
    minStartDate.setFullYear(minStartDate.getFullYear() - 1);
    let maxEndDate = new Date();
    maxEndDate.setFullYear(maxEndDate.getFullYear() + 10);

    document.getElementById('projectFormStartDate').value = new Date().toLocaleDateString("en-CA");
    document.getElementById('projectFormStartDate').setAttribute("min", minStartDate.toLocaleDateString("en-CA"));
    document.getElementById('projectFormStartDate').setAttribute("max", maxEndDate.toLocaleDateString("en-CA"));

    let endDate = new Date();
    endDate.setMonth(endDate.getMonth() + 8);


    document.getElementById('projectFormEndDate').value = endDate.toLocaleDateString("en-CA");

    document.getElementById('projectFormEndDate').setAttribute("min", minStartDate.toLocaleDateString("en-CA"));
    document.getElementById('projectFormEndDate').setAttribute("max", maxEndDate.toLocaleDateString("en-CA"));

    document.getElementById('projectFormDescription').value = "";

    document.getElementById('projectFormCreateBtn').hidden = false
    document.getElementById('projectFormEditBtn').hidden = true
    updateFormButton()
}

/**
 * Determines if the save button should be disabled
 */
function updateFormButton() {
    let formButton = document.getElementById('projectFormSubmitButton')
    projectFormUpdate = true
    if (checkProjectName() && checkProjectDates() && checkProjectDescription()) {
        formButton.disabled = false
    } else {
        formButton.disabled = true
    }
    projectFormUpdate = false
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

/**
 * Sends a delete request to the server and updates the delete modal
 * @param {int} projectId 
 */
 function deleteProject(projectId) {
    let httpRequest = new XMLHttpRequest();

    let modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('conformationModal'))
    let modalError = document.getElementById('projectDeleteModalError')

    httpRequest.onreadystatechange = updateModal(httpRequest, modal, modalError)

    httpRequest.open('DELETE', apiPrefix + `/project/${projectId}`);
    httpRequest.send();
}