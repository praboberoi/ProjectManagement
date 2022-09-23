const evidenceTitleError = document.getElementById('evidenceTitleError')
const evidenceDescriptionError = document.getElementById('evidenceDescriptionError');
const evidenceDescription = document.getElementById('evidence-description');
const evidence_project = document.getElementById("evidence-project");
const evidenceDateElement = document.getElementById("evidence-date");
const evidenceDateErrorElement = document.getElementById("evidenceDateError");

let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
})
let evidenceCreateBtn = document.getElementById('evidenceFormSubmitButton')

evidenceCreateBtn.disabled = true;
evidenceProjectChange();

function saveEvidence() {
    let httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = () => processAction(httpRequest)

    httpRequest.open('POST', apiPrefix + `/evidence/${userId}/saveEvidence`);

    let formData = new FormData(document.forms.createEvidenceForm)

    httpRequest.send(formData);
}

function checkEvidenceTitle() {
    const evidenceTitle = document.getElementById('evidence-title');
    let evidenceTitleStrip = evidenceTitle.value.trim()
    let charMessage = document.getElementById("evidenceCharCount");
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
}

function checkEvidenceDate() {
    const value = evidence_project.options[evidence_project.selectedIndex]
    if (evidenceDateElement.value > value.dataset.enddate || evidenceDateElement.value < value.dataset.startdate) {
        evidenceDateErrorElement.innerText = "The evidence date must be within the selected project range"
    } else {
        evidenceDateErrorElement.innerText = ""
    }
    checkCreateButton();

}

function evidenceProjectChange() {
    const evidence_project = document.getElementById("evidence-project");
    const value = evidence_project.options[evidence_project.selectedIndex]
    evidenceDateElement.min = value.dataset.startdate;
    evidenceDateElement.max = value.dataset.enddate;
    checkEvidenceDate();
}

function checkEvidenceDescription() {
    let evidenceDescriptionStrip = evidenceDescription.value.trim()
    if (evidenceDescriptionStrip.length < 2) {
        evidenceDescriptionError.innerText = "Evidence description must be at least 2 characters"
    } else if (evidenceDescriptionStrip.length >= 200) {
        evidenceDescriptionError.innerText = "Evidence description must be less that 200 characters"
    } else {
        evidenceDescriptionError.innerText = ""
    }
    checkCreateButton();
}

function checkCreateButton() {
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
