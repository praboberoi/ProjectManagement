let messageDanger = document.getElementById("messageDanger");
let messageSuccess = document.getElementById("messageSuccess");
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
    stompClient.subscribe('/element/user/', updateUser);
}

/**
 * Replaces the relevant component of the user table
 * @param message Message with userId
 */
function updateUser(message) {
    let array = message.body.split(' ')
    let userId = array[0]
    let userInfo = document.getElementById(`user` + userId + `Row`)
    if (userInfo) {
        let httpRequest = new XMLHttpRequest();
        httpRequest.open('GET', window.location.pathname + `/` + userId + '/info');
        httpRequest.onreadystatechange = () => updateElement(httpRequest, userInfo)
        httpRequest.send();
    }
}

/**
 * Replaces the old http component with the new one contained in the request
 * @param httpRequest Request containing a model view element
 * @param element The element to replace
 * @param errorMessage Optional variable, changes the default error message location
 */
function updateElement(httpRequest, element, errorMessage = messageDanger){
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        errorMessage.innerText = ""
        if (httpRequest.status === 200) {
            element.innerHTML = httpRequest.responseText;
        } else if (httpRequest.status === 400) {
            errorMessage.innerText = "Bad Request";
        } else if (httpRequest.status === 404) {
            errorMessage.innerText = "Unable to load " + element.id;
        } else {
            errorMessage.innerText = "Something went wrong.";
        }
    }
}


/**
 * Increment page count and load the new data
 * @returns 
 */
function nextPage() {
    getUserDataTable(page + 1);
}

/**
 * Decrement page count and load the new data
 * @returns 
 */
function prevPage() {
    getUserDataTable(page - 1);
}

/**
 * Changes the sorting order of the userlist and get the new table
 * @param field Field to sort
 */
function sortByField(field) {
    if (sortField === field) {
        isAsc = !isAsc
    } else {
        sortField = field;
        isAsc = true;
    }
    getUserDataTable(0)
}

/**
 * Makes a call to the server and replaces the current user data table with the new one
 */
function getUserDataTable(newPage) {
    messageDanger.hidden = true;
    messageSuccess.hidden = true;
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                document.getElementById("userListDataTable").innerHTML = httpRequest.responseText;
                page = newPage;
            } else if (httpRequest.status === 400) {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = "Bad Request";
            } 
        }
    }

    httpRequest.open('GET', apiPrefix + '/usersList?page=' + newPage + '&limit=' + 10 + '&order=' + sortField + '&asc=' + (isAsc?0:1));
    retrieving = true;
    httpRequest.send();
};

/**
 * Posts the new role for the user to have deleted from the server. Once
 * completed, reloads user table.
 * @param {UserRole} role
 * @param {int} userId
 */
function removeRole(role, userId) {
    httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function (qualifiedName, value) {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                const roleElement = document.getElementById(`user${userId}Role${role}`)
                roleElement.remove()
                getUserDataTable(page)
                messageDanger.hidden = true;

                messageSuccess.hidden = false;
                messageSuccess.innerText = httpRequest.response

                } else if (httpRequest.status === 400) {
                    messageDanger.hidden = false;
                    messageSuccess.hidden = true;
                    messageDanger.innerText = "Bad Request";
                } else {
                    messageDanger.hidden = false;
                    messageSuccess.hidden = true;
                    messageDanger.innerText = httpRequest.response;
                }
            }
        }


    httpRequest.open('DELETE', apiPrefix + '/usersList/removeRole', true);
    httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    httpRequest.send("role=" + role + "&userId=" + userId);
}

/**
 * Posts the new role for the user to have added to the server. Once
 * completed, replaces user row with updated row.
 * @param {int} userId
 * @param {UserRole} role
 */
function addRole(userId, role) {
    httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                messageDanger.hidden = true;
                getUserDataTable(page)
                messageSuccess.hidden = false;
                messageSuccess.innerText = httpRequest.response
            } else if (httpRequest.status === 400) {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = "Bad Request";
            } else {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = httpRequest.response;
            }
        }
    }

    httpRequest.open('POST', apiPrefix + '/user/' + userId + '/addRole', true);
    httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    httpRequest.send("role=" + role);
}