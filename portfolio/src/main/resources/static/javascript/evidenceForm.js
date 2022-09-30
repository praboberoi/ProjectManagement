let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
})

let evidenceTitleEditing = null;
let evidenceDateEditing = null;
let evidenceDescriptionEditing = null;
let evidenceProjectIdEditing = null;
const emojiRegx = /\p{Extended_Pictographic}/u;

/**
 * Calls the server to save the evidence
 */
function saveEvidence() {
    let modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('evidenceFormModal'))
    let modalError = document.getElementById('evidenceFormModalError')

    let httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = () => updateModal(httpRequest, modal, modalError)

    httpRequest.open('POST', apiPrefix + `/evidence`);

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
    } else if (emojiRegx.test(evidenceTitleStrip)){
        evidenceTitleError.innerText = "Evidence title must contain an emoji"
    } else {
        evidenceTitleError.innerText = ""
    }
    checkCreateButton()
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
}

/**
 * Updates the modal form based on the data received from the server
 * @param httpRequest the current http request to the server
 * @param evidenceProjectId the project ID of the evidence
 * @param modalTitle modal title based on the selection made
 */
function updateEvidenceModalForm(httpRequest, modalTitle) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            document.getElementById('evidenceForm').outerHTML = httpRequest.responseText;
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
    } else if (emojiRegx.test(evidenceDescriptionStrip)){
        evidenceDescriptionError.innerText = "Evidence description must contain an emoji"
    } else {
        evidenceDescriptionError.innerText = ""
    }
    checkCreateButton();
}
/**
 * Disables or enables the create button
 */
function checkCreateButton() {
    let evidenceCreateBtn = document.getElementById('evidenceFormSubmitButton')
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

    const date = document.getElementById('evidence-date').value;
    const description = document.getElementById('evidence-description').value.trim();
    const title = document.getElementById('evidence-title').value.trim();
    const projectId = document.getElementById('evidence-project').value;
    
    if (date.length === 0 || description.length === 0 || title.length === 0 || emojiRegx.test(title)  || emojiRegx.test(description) ||
        (date === evidenceDateEditing && description === evidenceDescriptionEditing && title === evidenceTitleEditing && projectId === evidenceProjectIdEditing)) {
            evidenceCreateBtn.disabled = true;
    } 
}

/**
 * Server call to create a new evidence
 */
function createNewEvidence() {
    evidenceTitleEditing = null;
    evidenceDateEditing = null;
    evidenceDescriptionEditing = null;
    evidenceProjectIdEditing = null;

    let httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', apiPrefix + `/evidence/getNewEvidence`)
    httpRequest.onreadystatechange = () => updateEvidenceModalForm(httpRequest, "Create New Evidence");
    httpRequest.send();
}

/**
 * Obtains the evidence from the server to update an existing evidence
 * @param evidenceId the ID of the evidence selected
 */
function editEvidence(evidenceId) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', apiPrefix + `/evidence/${evidenceId}/editEvidence`)
    httpRequest.onreadystatechange = () => updateEvidenceModalForm(httpRequest, "Update Evidence");
    httpRequest.send();


    stompClient.publish({ destination: "/app/evidence/edit", body: JSON.stringify({ 'active': true, 'evidenceId': evidenceId, 'userId': userId }) });
}

/**
 * Opens the modal
 */
function openEvidenceModal() {
    evidenceTitleEditing = document.getElementById('evidence-title').value
    evidenceDateEditing = document.getElementById('evidence-date').value;
    evidenceDescriptionEditing = document.getElementById('evidence-description').value;
    evidenceProjectIdEditing = document.getElementById('evidence-project').value;
    
    evidenceProjectChange();
    const modalElement = document.getElementById('evidenceFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();
}

function updateEditMessage() {
    const evidenceId = document.getElementById('evidenceId').value
    if (document.getElementById(`evidence-${evidenceId}-message-div`) !== null) {
        document.getElementById(`evidence-${evidenceId}-message-div`).hidden = true
        document.getElementById(`evidence-${evidenceId}-btns-div`).hidden = false
    }
    stompClient.publish({ destination: "/app/evidence/edit", body: JSON.stringify({ 'active': false, 'evidenceId': evidenceId, 'userId': userId }) });
}

/**
 * Updates the evidence deletion modal
 * @param evidenceId the ID of the evidence chosen to be deleted
 * @param evidenceTitle the title of the evidence chosen to be deleted
 */
function updateDeleteDetails(evidenceId, evidenceTitle) {
    document.getElementById('deleteMessage').innerText = `Are you sure you want to delete ${evidenceTitle}?`

    document.getElementById('evidenceModalDeleteBtn').onclick = function () {
        deleteEvidence(evidenceId);
    }

}

/**
 * Replaces the old messages with the new one contained in the request
 * @param httpRequest Request containing a model view element
 */
function processAction(httpRequest) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            messageSuccess.hidden = false
            messageDanger.hidden = true;
            messageSuccess.innerText = httpRequest.responseText;
        } else if (httpRequest.status === 400) {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = httpRequest.responseText;
        } else {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = "Something went wrong.";
        }
    }
}

/**
 * Updates the error message and removes the modal if there is no issues
 * @param httpRequest Request made to the server
 * @param modal Which modal is being edited
 * @param modalError Error message div that displays an error
 */
function updateModal(httpRequest, modal, modalError) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            modalError.innerText = ""
            messageSuccess.innerText = httpRequest.responseText;
            modal.hide()
        } else if (httpRequest.status === 500) {
            messageSuccess.innerText = ""
            modalError.innerText = "An error occurred on the server, please try again later";
        } else if (httpRequest.status === 400) {
            messageSuccess.innerText = ""
            modalError.innerText = httpRequest.responseText;
        } else {
            messageSuccess.innerText = ""
            modalError.innerText = "Something went wrong.";
        }
    }
}

document.getElementById('evidenceFormModal').addEventListener('hidden.bs.modal', function () {
    let userId = document.getElementById("userId").value
    let evidenceId = document.getElementById("evidenceId").value
    stompClient.publish({ destination: "/app/evidence/edit", body: JSON.stringify({ 'active': false, 'evidenceId': evidenceId, 'userId': userId }) });
});
