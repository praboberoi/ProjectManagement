let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
})

const userId = document.getElementById('userId').value;

function saveEvidence() {
    let httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = () => processAction(httpRequest)

    httpRequest.open('POST', apiPrefix + `/evidence/${userId}/saveEvidence`);

    let formData = new FormData(document.forms.createEvidenceForm)

    httpRequest.send(formData);
}

function checkEvidenceTitle() {
    const evidenceTitle = document.getElementById('evidence-title');
    let charMessage = document.getElementById("evidenceCharCount");
    let evidenceNameError = document.getElementById('evidenceDateError')
    let evidenceCreateBtn = document.getElementById('evidenceFormSubmitButton')
    let charCount = evidenceTitle.value.length;
    charMessage.innerText = charCount + ' ';

    if (evidenceTitle.value.length === 1) {
        evidenceCreateBtn.disabled = true;
        evidenceNameError.innerText = "Cannot have only one character for the name"
    }
    if (evidenceTitle.value.)



}
