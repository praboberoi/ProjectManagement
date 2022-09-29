/**
 * Script for setting the minimum and maximum values for start and end dates of the selected sprint.
 */

// Regular expression for Sprint Name field. No leading white spaces or empty field.
const sprintNameRegex = /^\S/;
const emojiRegx = /\p{Extended_Pictographic}/u;
const projectId = document.getElementById("projectId").value;
const sprintStartDateElement = document.getElementById("sprint-start-date");
const sprintEndDateElement = document.getElementById("sprint-end-date");
const labelElement = document.getElementById('sprint-label');
let currentSprintId;
const sprintStartDateError = document.getElementById('startDateError');
const sprintEndDateError = document.getElementById('endDateError');
const sprintNameElement = document.getElementById('sprint-name');
const sprintDescriptionElement = document.getElementById('sprint-description');

/**
 * Function for error validation of Sprint Name field.
 * Display error message if input is invalid.
  */
function checkSprintName() {
    let sprintName = document.getElementById('sprint-name');
    let sprintNameError = document.getElementById('sprintNameError');
    if (sprintName.value.length < 1) {
        sprintName.classList.add("formError");
        sprintNameError.innerText = "Sprint name must not be empty";
    } else if (sprintName.value.length > 50) {
        sprintName.classList.add("formError");
        sprintNameError.innerText = "Sprint name must be less than 50 characters";
    } else if (! sprintNameRegex.test(sprintName.value)) {
        sprintName.classList.add("formError");
        sprintNameError.innerText = "Sprint name must not start with empty space";
    } else if (emojiRegx.test(sprintName.value)) {
        sprintName.classList.add("formError");
        sprintNameError.innerText = "Sprint name must not contain an emoji";
    } else {
        sprintName.classList.remove("formError");
        sprintNameError.innerText = null;
    }
}

/**
 * Checks that the start and end dates of the sprint are valid
 */
function checkSprintDates() {
    const startDate = sprintStartDateElement.value;
    const endDate = sprintEndDateElement.value;
    sprintStartDateElement.setCustomValidity("");
    sprintEndDateElement.setCustomValidity("");

    checkStartDate();
    checkEndDate();

    if (startDate > endDate ) {
        sprintStartDateError.innerText = "Start date must be before the end date.";
        sprintEndDateError.innerText = "End date must be after the start date";
        sprintStartDateElement.classList.add("formError");
        sprintEndDateElement.classList.add("formError");
        return;
    }

    if (
    sprintStartDateError.innerText == "" &&
    sprintEndDateError.innerText == "" ) {
        verifyOverlap(startDate, endDate);
    }
}

/**
 * Checks that the start date of the sprint is within the project
 */
function checkStartDate() {
    const startDate = new Date(sprintStartDateElement.value);

    if (startDate < projectStartDate) {
        sprintStartDateError.innerText = "Sprint must start after " + projectStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        sprintStartDateElement.classList.add("formError");
        return;
    } else if (startDate > projectEndDate) {
        startDateErrorsprintStartDateError.innerText = "Sprint must start before the project ends";
        sprintStartDateElement.classList.add("formError");
        return;
    }

    sprintStartDateError.innerText = "";
    sprintStartDateElement.classList.remove("formError")
}

/**
 * Checks that the end date of the sprint is within the project
 */
function checkEndDate() {
    const endDate = new Date(sprintEndDateElement.value);

    if (endDate < projectStartDate) {
        sprintEndDateError.innerText = "Sprint must start after " + projectStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        sprintEndDateElement.classList.add("formError");
        return;
    } else if (endDate > projectEndDate) {
        sprintEndDateError.innerText = "Sprint must end before " + projectEndDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        sprintEndDateElement.classList.add("formError");
        return;
    }

    sprintEndDateError.innerText = "";
    sprintEndDateElement.classList.remove("formError")
}

/**
 * Updates the characters remaining in the description.
 */
function checkSprintDescription () {
    let descriptionElement = document.getElementById("sprint-description");
    let descErrorElement = document.getElementById("descriptionError");

    let charMessage = document.getElementById("charCount");
    let charCount = descriptionElement.value.length;
    charMessage.innerText = charCount + ' '

    if (descriptionElement.value.length > 250)
    {
        descErrorElement.classList.add("formError");
        descErrorElement.innerText = "Description must be less than 250 characters."

    } else if (emojiRegx.test(descriptionElement.value)) {
        descErrorElement.classList.add("formError");
        descErrorElement.innerText = "Sprint description must not contain an emoji";
    } else {
        descErrorElement.classList.remove("formError");
        descErrorElement.innerText = null;
    }

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
                    sprintStartDateError.innerText = httpRequest.response;
                    sprintStartDateElement.classList.add("formError");
                    sprintEndDateElement.classList.add("formError");
                }
            }
        }
    }

    httpRequest.open('POST', '/project/' + projectId + '/verifySprint', true);
    httpRequest.setRequestHeader('Content-type', 'application/json');
    httpRequest.send(JSON.stringify({'sprintId': sprintId, 'sprintLabel': labelElement.value,
        'sprintName': sprintNameElement.value, 'startDate': startDate, 'endDate': endDate, 'description': sprintDescriptionElement.value}));
}

/**
 * Send a request to the server to save the newly created sprint
 */
function saveSprint() {
    let modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('sprintFormModal'))
    let modalError = document.getElementById('sprintFormModalError')
    if (validateForm()) {
        let httpRequest = new XMLHttpRequest();

        httpRequest.onreadystatechange = () => updateModal(httpRequest, modal, modalError)

        httpRequest.open('POST', apiPrefix + `/project/${projectId}/sprint`);

        let formData = new FormData(document.forms.createSprintForm)

        httpRequest.send(formData);
    }
}

/**
 * Populate sprint creation or editing modal with proper values
 * @param isEdit Bool for if create or edit selected
 */
function populateSprintModal(isEdit, sprintId, sprintName, startDate, endDate, description) {
    currentSprintId  = sprintId;
    projectStartDate  = new Date(document.getElementById("projectStartDate").value);
}