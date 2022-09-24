let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
})

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
 * Updates the submission button and current char count displayed to the user.
 */
function checkEvidenceTitle(){
    const evidenceTitle = document.getElementById('evidence-title');
    let charMessage = document.getElementById("evidenceCharCount");
    let charCount = evidenceTitle.value.length;
    charMessage.innerText = charCount + ' ';
    document.getElementById('evidenceFormSubmitButton').disabled = evidenceTitle.value.length === 0;

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
 */
function editEvidence(evidenceId, evidenceProjectId) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', `${window.location.pathname}/${evidenceId}/editEvidence`)
    httpRequest.onreadystatechange = () => updateEvidenceModalForm(httpRequest, evidenceProjectId, "Update Evidence");
    httpRequest.send();

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

/**
 * Checks the evidence description to update the status of the submission button
 */
function checkEvidenceDescription() {
    const description = document.getElementById('evidence-description').value;
    document.getElementById('evidenceFormSubmitButton').disabled = description.length === 0;

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