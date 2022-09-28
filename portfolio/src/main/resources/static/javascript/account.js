let stompClient = null;


/**
 * Runs the connect function when the document is loaded
 */
document.addEventListener('DOMContentLoaded', function() {
    connect();
})

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
    stompClient.subscribe('/element/user/' + userId + '/roles', updateRoles);
}

/**
 * Replaces roles of the user
 * @param message Message with userId
 */
function updateRoles() {
    let httpRequest = new XMLHttpRequest();
    roleElement = document.getElementById("roleList")
    httpRequest.open('GET', window.location.pathname + `/roles`);
    httpRequest.onreadystatechange = () => updateRoleElement(httpRequest, roleElement)
    httpRequest.send();
}

/**
 * Replaces the old http component with the new one contained in the request
 * @param httpRequest Request containing a model view element
 * @param element The element to replace
 */
function updateRoleElement(httpRequest, element){
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        element.innerHTML = httpRequest.responseText;
    }
}