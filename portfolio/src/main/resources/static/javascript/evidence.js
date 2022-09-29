let stompClient = null;

/**
 * Connects to the websocket server
 */
function connect() {
    let websocketProtocol = window.location.protocol === 'http:'?'ws://':'wss://'
    stompClient = new StompJs.Client({
        brokerURL: websocketProtocol + window.location.host + apiPrefix + '/lensfolio-websocket',
        debug: function(str) {
            // console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = function () {
        subscribe()
        document.getElementById("websocket-status").value = "connected"
    };

    stompClient.onStompError = function () {
        console.log('Websocket communication error')
    }

    stompClient.activate();
}

/**
 * Subscribes to the required websocket notification channels
 */
function subscribe() {
    stompClient.subscribe('/element/evidence/' + userId, updateEvidencePage);
    getEvidenceList();
}

/**
 * Runs the connect function when the document is loaded
 */
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById("websocket-status").value !== "connected") {
        connect();
    }
})

/**
 * Updates the evidence page based on the notification message received from the server.
 * @param message
 */
function updateEvidencePage(message) {
    const evidenceNotification  = JSON.parse(message.body)
    const evidenceId = evidenceNotification.evidenceId
    const action = evidenceNotification.action
    const firstEvidenceId = evidenceNotification.firstEvidenceId
    const selectedEvidenceIdElement = document.getElementById('selectedEvidenceId')
    const selectedEvidenceId = selectedEvidenceIdElement === null ? 0 : parseInt(selectedEvidenceIdElement.value)

    getEvidenceList()

    if (action === "deleted" && selectedEvidenceId === evidenceId && firstEvidenceId !== 0) {
        getSelectedEvidence(firstEvidenceId)
        document.getElementById('selectedEvidence').hidden = false
    }

    else if (action === "deleted" && firstEvidenceId === 0) {
        document.getElementById('selectedEvidence').hidden = true
        selectedEvidenceIdElement.value = 0
    }

    else if (action === 'saved' && evidenceId !== 0 && selectedEvidenceId === evidenceId) {
        getSelectedEvidence(evidenceId)
        document.getElementById('selectedEvidence').hidden = false
    }

    else if (action === 'saved' && firstEvidenceId !== 0 && selectedEvidenceId === 0) {
        getSelectedEvidence(firstEvidenceId)
        document.getElementById('selectedEvidence').hidden = false
    }

    else if (action === 'editing') {
        const messageDiv = document.getElementById(`evidence-${evidenceId}-message-div`)
        messageDiv.innerHTML = `<p class="h6 text-align-start font-italic text-black-50">${evidenceNotification.userUpdating} is currently editing</p>`
        messageDiv.hidden = false
        document.getElementById(`evidence-${evidenceId}-btns-div`).hidden = true

        const messageDivResponsive = document.getElementById(`evidence-responsive-message-div`)
        messageDivResponsive.innerHTML = `<p class="h6 text-center font-italic text-black-50">${evidenceNotification.userUpdating} is currently editing</p>`
        messageDivResponsive.hidden = false
        document.getElementById('edit-evidence-btn').hidden = true
        document.getElementById('delete-evidence-btn').hidden = true
    }

    else if (action === 'finished') {
        document.getElementById(`evidence-${evidenceId}-message-div`).hidden = true
        document.getElementById(`evidence-${evidenceId}-btns-div`).hidden = false

        document.getElementById(`evidence-responsive-message-div`).hidden = true
        document.getElementById('edit-evidence-btn').hidden = false
        document.getElementById('delete-evidence-btn').hidden = false
    }

}

/**
 * Obtains the evidence list from the server
 */
function getEvidenceList() {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = () => updateElement(httpRequest,document.getElementById('evidence-list'));
    httpRequest.open('GET', apiPrefix + `/user/${userId}/evidence/getEvidenceList`);
    httpRequest.send();
}

/**
 * Updates the given element form with the data reviewed from the server
 * @param httpRequest the current http request to the server
 * @param element the element to be updated
 */
function updateElement(httpRequest, element) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            element.outerHTML = httpRequest.responseText;
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


/**
 * Makes a call to the server and replaces the current evidence with the new one
 */
function getSelectedEvidence(selectedEvidenceId) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange =  () =>  updateElement(httpRequest, document.getElementById("selectedEvidence"));
    httpRequest.open('GET', apiPrefix + `/evidence/${userId}/${selectedEvidenceId}`);
    httpRequest.send();
}