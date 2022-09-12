let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
  return new bootstrap.Tooltip(tooltipTriggerEl)
})
let stompClient = null;

/**
 * Connects to the websocket server
 */
function connect() {
    let websocketProtocol = window.location.protocol === 'http:'?'ws://':'wss://'
    stompClient = new StompJs.Client({
        brokerURL: websocketProtocol + window.location.host + apiPrefix + '/lensfolio-websocket',
        // debug: function(str) {
        //     console.log(str);
        // },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = function () {
        console.log('Active updating enabled');
        subscribe()
        document.getElementById("websocket-status").value = "connected"
    };

    stompClient.onStompError = function () {
        console.log('Websocket communication error')
    }

    stompClient.activate();
}

/**
 * Connects to web socket on page load
 */
document.addEventListener('DOMContentLoaded', function() {
    connect();
});


/**
 * Attempts to connect to the git repository using the details provided
 * @param event Form submit event
 */
function connectTest(event) {
    event.preventDefault()

    let message = document.getElementById("connection-message")

    let projectId = document.getElementById("git-project-id").value
    let accessToken = document.getElementById("git-access-token").value
    let hostAddress = document.getElementById("git-host-address").value

    fetch(hostAddress + "/api/v4/projects/" + projectId, {
        method: 'GET',
        headers: {
            'PRIVATE-TOKEN': accessToken, //'sVMvHmHxhJeqdZBBchDB' <-- This is a project token for an empty gitlab repo (id = 13964) that I have created for testing purposes
            'Content-Type': 'application/json',
        },
    }).then(async (response) => {
        const repo = await  response.json();
        if (!repo.hasOwnProperty('id')) {
            message.innerText = "Repo not found"
            return
        }
        document.getElementById("git-project-name").value = repo.name
        message.innerText = "Connected to repo: " + repo.name
    }).catch((error) => {
        document.getElementById("git-project-name").value = ""
        message.innerText = "Error connecting to repository"
    });

    
}

/**
 * Subscribes to the required websocket notification channels
 */
function subscribe() {
    stompClient.subscribe('/element/account/', updateUser);
}


/**
 * Replaces the relevant component of the user table
 * @param message Message with user id
 */
function updateUser(message) {
    let array = message.body.split(' ')
    let userId = array[1]
    let httpRequest = new XMLHttpRequest();

    const xpath = `//td[text()='` + userId + `']`
    let groupListElement = document.getElementById("userListDataTable")
    const changedUserId = document.evaluate(xpath, groupListElement, null, XPathResult.FIRST_ORDERED_NODE_TYPE).singleNodeValue.textContent

    if (changedUserId === userId) {
        httpRequest.open('GET', window.location.pathname + `/members`);
        httpRequest.onreadystatechange = () => updateElement(httpRequest, groupListElement)
        httpRequest.send();
    }
}

/**
 * Replaces the old http component with the new one contained in the request
 * @param httpRequest Request containing a model view element
 * @param element The element to replace
 */
function updateElement(httpRequest, element){
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            element.innerHTML = httpRequest.responseText;
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
