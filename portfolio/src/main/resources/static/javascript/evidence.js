/**
 * Makes a call to the server and replaces the current evidence with the new one
 */
function getSelectedEvidence(selectedEvidenceId, ownerId) {
    console.log("JAVASCRIPT")
    console.log(ownerId)
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                console.log(httpRequest.responseText);
                console.log('aaaaaaaaaaaaaaa')
                document.getElementById("selectedEvidence").outerHTML = httpRequest.responseText;
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

    httpRequest.open('GET', apiPrefix + `/evidence/${ownerId}/${selectedEvidenceId}`);
    httpRequest.send();
}