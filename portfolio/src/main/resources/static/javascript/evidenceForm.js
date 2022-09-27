let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
})

const userId = document.getElementById('userId').value;


function checkEvidenceTitle(){
    const evidenceTitle = document.getElementById('evidence-title');
    let charMessage = document.getElementById("evidenceCharCount");
    let charCount = evidenceTitle.value.length;
    charMessage.innerText = charCount + ' ';


}
