let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
})
let evidenceCreateBtn = document.getElementById('evidenceFormSubmitButton')

evidenceCreateBtn.disabled = true;
evidenceProjectChange();
let evidenceTitleEditing = null;
let evidenceDateEditing = null;
let evidenceDescriptionEditing = null;
let evidenceProjectIdEditing = null;

/**
 * Calls the server to save the evidence
 */
function saveEvidence() {
    let httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = () => processAction(httpRequest)

    httpRequest.open('POST', apiPrefix + `/evidence/${userId}/saveEvidence`);

    let formData = new FormData(document.forms.createEvidenceForm)

    httpRequest.send(formData);
}

/**
 * Validation for the evidence title
 */
function checkEvidenceTitle() {
    let evidenceTitleError = document.getElementById('evidenceTitleError')
    let evidenceTitle = document.getElementById('evidence-title');
    let evidenceTitleStrip = evidenceTitle.value.trim()
    let charMessage = document.getElementById("evidenceTitleCharCount");
    let charCount = evidenceTitleStrip.length;
    charMessage.innerText = charCount + ' ';
    if (evidenceTitleStrip.length <= 1) {
        evidenceTitleError.innerText = "Evidence title must be at least 2 characters"
    } else if (!/[a-zA-Z]/.test(evidenceTitleStrip)) {
        evidenceTitleError.innerText = "Evidence title must contain some letters"
    } else {
        evidenceTitleError.innerText = ""
    }
    checkCreateButton();
    updateSubmissionButton()

}

/**
 * Validation for the evidence date
 */
function checkEvidenceDate() {
    let evidence_project = document.getElementById("evidence-project");
    let evidenceDateElement = document.getElementById("evidence-date");
    let evidenceDateErrorElement = document.getElementById("evidenceDateError");
    let value = evidence_project.options[evidence_project.selectedIndex]
    if (evidenceDateElement.value > value.dataset.enddate || evidenceDateElement.value < value.dataset.startdate) {
        evidenceDateErrorElement.innerText = "The evidence date must be within the selected project range"
    } else {
        evidenceDateErrorElement.innerText = ""
    }
    checkCreateButton();
    updateSubmissionButton()
}

    /**
 * Updates the modal form based on the data received from the server
 * @param httpRequest the current http request to the server
 * @param evidenceProjectId the project ID of the evidence
 * @param modalTitle modal title based on the selection made
 */
function updateEvidenceModalForm(httpRequest, evidenceProjectId, modalTitle) {

    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            document.getElementById('evidenceForm').outerHTML = httpRequest.responseText;
            evidenceProjectId > 0 && (document.getElementById(`project-${evidenceProjectId}`).selected = true);
            document.getElementById('evidenceFormTitle').innerText = modalTitle
            openEvidenceModal();

        } else if (httpRequest.status === 400) {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = "Bad Request";

        } else if (httpRequest.status === 404) {
            const serverMessages = document.getElementById('serverMessages');
            serverMessages.outerHTML = httpRequest.responseText;
            messageDanger.hidden = false;
            messageSuccess.hidden = true;

        } else {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = "Something went wrong.";
        }
    }
}
/**
 * Changes the dates min and max value when the project changes
 */
function evidenceProjectChange() {
    let evidenceDateElement = document.getElementById("evidence-date");
    let evidence_project = document.getElementById("evidence-project");
    let value = evidence_project.options[evidence_project.selectedIndex]
    evidenceDateElement.min = value.dataset.startdate;
    evidenceDateElement.max = value.dataset.enddate;
    checkEvidenceDate();
}
/**
 * Validation for the evidence description
 */
function checkEvidenceDescription() {
    let evidenceDescriptionError = document.getElementById('evidenceDescriptionError');
    let evidenceDescription = document.getElementById('evidence-description');
    let evidenceDescriptionStrip = evidenceDescription.value.trim()
    let charMessage = document.getElementById("evidenceDescriptionCharCount");
    let charCount = evidenceDescriptionStrip.length;
    charMessage.innerText = charCount + ' ';
    if (evidenceDescriptionStrip.length < 2) {
        evidenceDescriptionError.innerText = "Evidence description must be at least 2 characters"
    } else if (evidenceDescriptionStrip.length > 200) {
        evidenceDescriptionError.innerText = "Evidence description must be equal or less that 200 characters"
    } else {
        evidenceDescriptionError.innerText = ""
    }
    checkCreateButton();
    updateSubmissionButton()
}
/**
 * Disables or enables the create button
 */
function checkCreateButton() {
    let evidenceTitleError = document.getElementById('evidenceTitleError')
    let evidenceDescriptionError = document.getElementById('evidenceDescriptionError');
    let evidenceDescription = document.getElementById('evidence-description');
    let evidenceDateErrorElement = document.getElementById("evidenceDateError");
    let evidenceDescriptionStrip = evidenceDescription.value.trim()
    evidenceCreateBtn.disabled = false;

    if (evidenceTitleError.innerText !== "") {
        evidenceCreateBtn.disabled = true;
    } else if (evidenceDescriptionError.innerText !== "" || evidenceDescriptionStrip.length < 2) {
        evidenceCreateBtn.disabled = true;
    } else if (evidenceDateErrorElement.innerText !== "") {
        evidenceCreateBtn.disabled = true;
    }

}

/**
 * Server call to create a new evidence
 */
function createNewEvidence() {
    let httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', `${window.location.pathname}/getNewEvidence`)
    httpRequest.onreadystatechange = () => updateEvidenceModalForm(httpRequest, 0, "Create New Evidence");
    httpRequest.send();
}

/**
 * Obtains the evidence from the server to update an existing evidence
 * @param evidenceId the ID of the evidence selected
 * @param evidenceProjectId the project ID of the evidence
 * @param evidenceTitle title of selected evidence
 * @param evidenceDate date of the selected evidence
 * @param evidenceDescription description of the selected evidence
 */
function editEvidence(evidenceId, evidenceProjectId, evidenceTitle, evidenceDate, evidenceDescription) {
    evidenceTitleEditing = evidenceTitle;
    evidenceDateEditing = evidenceDate.substring(0, 10);
    evidenceDescriptionEditing = evidenceDescription;
    evidenceProjectIdEditing = document.getElementById('evidence-project').value;
    let httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', `${window.location.pathname}/${evidenceId}/editEvidence`)
    httpRequest.onreadystatechange = () => updateEvidenceModalForm(httpRequest, evidenceProjectId, "Update Evidence");
    httpRequest.send();
    stompClient.publish({destination: "/app/evidence/edit", body: JSON.stringify({'evidenceId': evidenceId, 'action': "editing", 'firstEvidenceId': 0, 'userUpdating': null, 'userId':userId, 'sessionId': 0})});
}

/**
 * Opens the modal
 */
function openEvidenceModal() {
    const modalElement = document.getElementById('evidenceFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();
}

function updateEditMessage() {

    const evidenceId = document.getElementById('evidenceId').value
    if(document.getElementById(`evidence-${evidenceId}-message-div`) !== null) {
        document.getElementById(`evidence-${evidenceId}-message-div`).hidden = true
        document.getElementById(`evidence-${evidenceId}-btns-div`).hidden = false
        stompClient.publish({destination: "/app/evidence/edit", body: JSON.stringify({'evidenceId': evidenceId, 'action': "finished", 'firstEvidenceId': 0, 'userUpdating': null, 'userId':userId})})
    }
}

/**
 * Updates the evidence deletion modal
 * @param evidenceId the ID of the evidence chosen to be deleted
 * @param evidenceTitle the title of the evidence chosen to be deleted
 */
function updateDeleteDetails(evidenceId, evidenceTitle) {
    document.getElementById('deleteMessage').innerText = `Are you sure you want to delete ${evidenceTitle}?`
    document.getElementById('deleteEvidenceForm').action = `${window.location.pathname}/${evidenceId}/deleteEvidence`

}

/**
 * Updates the status of submission button based on the input values entered
 */
function updateSubmissionButton() {
    const date = document.getElementById('evidence-date').value;
    const description = document.getElementById('evidence-description').value.trim();
    const title = document.getElementById('evidence-title').value.trim();
    const projectId = document.getElementById('evidence-project').value;

    if ((date.length === 0 || description.length === 0 || title.length === 0) ||
        (date === evidenceDateEditing && description === evidenceDescriptionEditing && title === evidenceTitleEditing && projectId === evidenceProjectIdEditing))

        document.getElementById('evidenceFormSubmitButton').disabled = true

    else
        document.getElementById('evidenceFormSubmitButton').disabled = false
}