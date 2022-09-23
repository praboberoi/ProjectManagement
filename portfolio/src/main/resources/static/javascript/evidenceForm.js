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
}

function updateEvidenceModalForm(httpRequest) {

    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            document.getElementById('evidenceForm').outerHTML = httpRequest.responseText;
        } else if (httpRequest.status === 400) {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = "Bad Request";
        } else {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = "Something went wrong.";
        }
    }
}

function editEvidence(evidenceId, evidenceProjectId) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', `${window.location.pathname}/${evidenceId}/evidence`)
    httpRequest.onreadystatechange = () => {
        updateEvidenceModalForm(httpRequest);
        document.getElementById('evidenceFormTitle').innerText = "Update Evidence";
        document.getElementById(`project-${evidenceProjectId}`).selected = true;
        document.getElementById('evidenceFormSubmitLabel').innerText = 'Save';
        document.getElementById('evidenceFromSubmitImg').src = `${apiPrefix}/icons/save-icon.svg`;
        const modalElement = document.getElementById('evidenceFormModal');
        const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
            keyword: false,
            backdrop: "static"
        });
        modal.show();
    }
    httpRequest.send();

}

function createNewEvidence() {
    let httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', `${window.location.pathname}/getNewEvidence`)
    httpRequest.onreadystatechange = () => {
        updateEvidenceModalForm(httpRequest);
        document.getElementById('evidenceFormTitle').innerText = "Create New Evidence";
        document.getElementById('evidenceFormSubmitLabel').innerText = 'Create';
        document.getElementById('evidenceFromSubmitImg').src = `${apiPrefix}/icons/create-icon.svg`;
        const modalElement = document.getElementById('evidenceFormModal');
        const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
            keyword: false,
            backdrop: "static"
        });
        modal.show();
    }
    httpRequest.send();
}
