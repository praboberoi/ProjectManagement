let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
})

function saveEvidence() {
    let httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = () => processAction(httpRequest)

    httpRequest.open('POST', apiPrefix + `/evidence/${userId}/saveEvidence`);

    let formData = new FormData(document.forms.createEvidenceForm)

    httpRequest.send(formData);
}

function checkEvidenceTitle(){
    const evidenceTitle = document.getElementById('evidence-title');
    let charMessage = document.getElementById("evidenceCharCount");
    let charCount = evidenceTitle.value.length;
    charMessage.innerText = charCount + ' ';
    document.getElementById('evidenceFormSubmitButton').disabled = evidenceTitle.value.length === 0;

}

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

function createNewEvidence() {

    let httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', `${window.location.pathname}/getNewEvidence`)
    httpRequest.onreadystatechange = () => updateEvidenceModalForm(httpRequest, 0, "Create New Evidence");
    httpRequest.send();
}

function editEvidence(evidenceId, evidenceProjectId) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', `${window.location.pathname}/${evidenceId}/editEvidence`)
    httpRequest.onreadystatechange = () => updateEvidenceModalForm(httpRequest, evidenceProjectId, "Update Evidence");
    httpRequest.send();

}
function openEvidenceModal() {
    const modalElement = document.getElementById('evidenceFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();
}

function checkEvidenceDescription() {
    const description = document.getElementById('evidence-description').value;
    document.getElementById('evidenceFormSubmitButton').disabled = description.length === 0;

}

function updateDeleteDetails(evidenceId, evidenceTitle) {
    document.getElementById('deleteMessage').innerText = `Are you sure you want to delete ${evidenceTitle}?`
    document.getElementById('deleteEvidenceForm').action = `${window.location.pathname}/${evidenceId}/deleteEvidence`

}