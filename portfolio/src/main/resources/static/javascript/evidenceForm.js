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


/**
 * Opens the modal to edit
 * @param evidenceId
 * @param evidenceTitle
 * @param projectName
 * @param evidenceProjectId
 * @param evidenceDateOccurred
 */
function editEvidence(evidenceId,evidenceTitle, projectName, evidenceProjectId,evidenceDateOccurred, projectList) {
    let httpRequest = new XMLHttpRequest();

    const evidenceForm = document.getElementById('evidenceForm');
    httpRequest.open('GET', `${window.location.pathname}/${evidenceId}/evidence`)
    httpRequest.onreadystatechange = () => {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                document.getElementById(`project-${evidenceProjectId}`).selected = true
                evidenceForm.outerHTML = httpRequest.responseText;
                const modalElement = document.getElementById('evidenceFormModal');
                const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
                    keyword: false,
                    backdrop: "static"
                });
                modal.show();
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
    httpRequest.send();

}
