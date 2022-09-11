const messageSucess = document.getElementById("messageSuccess");
const userId = document.getElementById("userId").innerText;

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
 * Runs when the message success message is on the account page after successful editing of an account.
 */
document.addEventListener('DOMContentLoaded', function() {
    if (messageSucess !== null) {

        connect();
        stompClient.publish({destination: "/app/account/edit", body: JSON.stringify({'active': false, 'userId': projectId, 'eventId': currentEventId})})
    }
});
