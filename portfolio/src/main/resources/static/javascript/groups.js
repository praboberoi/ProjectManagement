const emojiRegx = /\p{Extended_Pictographic}/u;

/**
 * Count down the characters remaining in the Group Short name, and check the length is between 3 and 50 characters.
 */
function checkShortName(event) {
    let groupShortNameElement = event.target;
    let groupShortNameErrorElement = groupShortNameElement.parentNode.querySelector('#shortNameError')
    let charMessage = groupShortNameElement.parentNode.querySelector("#charCount");
    let charCount = groupShortNameElement.value.length;
    charMessage.innerText = charCount + ' '
    if (charCount < 3 || charCount > 50) {
        groupShortNameElement.classList.add('formError');
        groupShortNameErrorElement.innerText = "Group short name must be between 3 and 50 characters."
        groupShortNameElement.setCustomValidity("Invalid Field")

    } else if (emojiRegx.test(groupShortNameElement.value)) {
        groupShortNameElement.classList.add("formError");
        groupShortNameErrorElement.innerText = "Group short name must not contain an emoji";
        groupShortNameElement.setCustomValidity("Invalid Field")

    } else {
        groupShortNameElement.classList.remove("formError");
        groupShortNameErrorElement.innerText = null;
        groupShortNameElement.setCustomValidity("");
    }
}

/**
 * Count down the characters remaining in the Group Long name, and check the length is between 3 and 100 characters.
 */
function checkLongName(event) {
    let groupLongNameElement = event.target;
    let groupLongNameErrorElement = groupLongNameElement.parentNode.querySelector("#longNameError")
    let charMessage = groupLongNameElement.parentNode.querySelector("#charCountLong");
    let charCount = groupLongNameElement.value.length;

    if (charCount < 3 || charCount > 100) {
        groupLongNameElement.classList.add('formError');
        groupLongNameErrorElement.innerText = "Group long name must be between 3 and 100 characters."
        groupLongNameElement.setCustomValidity("Invalid Field")

    } else if (emojiRegx.test(groupLongNameElement.value)) {
        groupLongNameElement.classList.add("formError");
        groupLongNameErrorElement.innerText = "Group long name must not contain an emoji";
        groupLongNameElement.setCustomValidity("Invalid Field")

    } else {
        groupLongNameElement.classList.remove("formError");
        groupLongNameErrorElement.innerText = null;
        groupLongNameElement.setCustomValidity("");
    }
    charMessage.innerText = charCount + ' '

}

/**
 * Makes a call to the server and replaces the current group list with the new one
 */
function updateGroupList() {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                document.getElementById("group-list").outerHTML = httpRequest.responseText;
            } else if (httpRequest.status === 400) {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = "Bad Request";
            }
        }
    }

    httpRequest.open('GET', apiPrefix + `/groups/list`);
    httpRequest.send();
}

/**
 * Sends a request to the server to create the group
 * @param event Form submit request
 */
function createGroup() {
    let httpRequest = new XMLHttpRequest();

    let createModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('createModal'))
    let modalError = document.getElementById('createModalError')
    httpRequest.onreadystatechange = () => updateModal(httpRequest, createModal, modalError)

    httpRequest.open('POST', apiPrefix + `/groups`);

    let formData = new FormData(document.forms.createGroupForm)

    httpRequest.send(formData);
}

/**
 * Sends a request to the server to save the group
 * @param event Form submit request
 */
function saveGroup() {
    let httpRequest = new XMLHttpRequest();

    let editModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('editModal'))
    let modalError = document.getElementById('editModalError')

    httpRequest.onreadystatechange = () => updateModal(httpRequest, editModal, modalError)

    httpRequest.open('POST', apiPrefix + `/groups`);

    let formData = new FormData(document.forms.editGroupForm)

    httpRequest.send(formData);
}

/**
 * Updates the error message and removes the modal if there is no issues
 * @param httpRequest Request made to the server
 * @param modal Which modal is being edited
 * @param modalError Error message div that displays an error
 */
function updateModal(httpRequest, modal, modalError) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            modalError.innerText = ""
            messageSuccess.innerText = httpRequest.responseText;
            modal.hide()
        } else if (httpRequest.status === 500) {
            messageSuccess.innerText = ""
            modalError.innerText = "An error occurred on the server, please try again later";
        } else if (httpRequest.status == 400) {
            messageSuccess.innerText = ""
            modalError.innerText = httpRequest.responseText;
        } else {
            messageSuccess.innerText = ""
            modalError.innerText = "Something went wrong.";
        }
    }
}

/**
 * Makes a call to the server and replaces the current group with the new one
 */
function getGroup(groupId) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                document.getElementById("group").outerHTML = httpRequest.responseText;
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

    httpRequest.open('GET', apiPrefix + `/groups/${groupId}`);
    httpRequest.send();
}

/**
 * Calls the server to delete the selected group and show the unassgned members group on success
 * @param groupId Id of the group to delete
 */
function deleteGroup(groupId) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                messageSuccess.hidden = false
                messageDanger.hidden = true;
                messageSuccess.innerText = httpRequest.responseText;
                getGroup("unassigned")
                updateGroupList()
            } else {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = httpRequest.responseText;
            }
        }
    }

    httpRequest.open('DELETE', apiPrefix + `/groups/${groupId}`);
    httpRequest.send();
}

/**
 * Selects and highlights the group members selected. Functions with shift and control clicking.
 * @param event The click event on the group member
 */
function selectUser(event) {
    let removeUserButton = document.querySelector('#remove-users')
    let moveUserButton = document.querySelector('#move-users')
    if (removeUserButton != null) {
        removeUserButton.disabled = false
    }
    if (moveUserButton != null) {
        moveUserButton.disabled = false
    }
    let userTable = document.querySelectorAll("#userListDataTable tr")

    if (event.shiftKey && (event.ctrlKey || event.metaKey)) {
        let selected = document.querySelector(".currently-selected")
        let table_elements = [].slice.call(userTable);

        let index_target = table_elements.indexOf(event.target.closest('tr'))
        let index_selected = table_elements.indexOf(selected)

        table_elements.splice(Math.min(index_target, index_selected), Math.abs(index_target - index_selected) + 1).forEach(element => {
            element.classList.add('table-info');
            element.classList.add('selected')
        });

    } else if (event.shiftKey) {
        let selected = document.querySelector(".currently-selected")
        let table_elements = [].slice.call(userTable);

        let index_target = table_elements.indexOf(event.target.closest('tr'))
        let index_selected = table_elements.indexOf(selected)

        document.querySelectorAll('.selected').forEach(row => {
            row.classList.remove('table-info');
            row.classList.remove('selected');
        })
        table_elements.splice(Math.min(index_target, index_selected), Math.abs(index_target - index_selected) + 1).forEach(element => {
            element.classList.add('table-info');
            element.classList.add('selected')
        });


    } else if (event.ctrlKey || event.metaKey) {
        document.querySelectorAll('.currently-selected').forEach(row => {
            row.classList.remove('currently-selected');
        })
        if (event.target.closest('tr').classList.contains('selected')) {
            event.target.closest('tr').classList.remove('table-info');
            event.target.closest('tr').classList.remove('selected');
            event.target.closest('tr').classList.add('currently-selected');
        } else {

            event.target.closest('tr').classList.add('currently-selected');
            event.target.closest('tr').classList.add('selected');
            event.target.closest('tr').classList.add('table-info');
        }
    } else {
        document.querySelectorAll('.selected, .currently-selected').forEach(row => {
            row.classList.remove('table-info');
            row.classList.remove('selected');
            row.classList.remove('currently-selected');
        })

        event.target.closest('tr').classList.add('currently-selected');
        event.target.closest('tr').classList.add('selected');
        event.target.closest('tr').classList.add('table-info');
    }
}

/**
 * Makes a call to the server and removes the selected members from the group
 * @param groupId Id of the selected group
 */
function removeUsers(groupId) {
    let httpRequest = new XMLHttpRequest();
    let userIds = []
    let errorCodes = [400, 403]
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                messageDanger.hidden = true;
                messageSuccess.hidden = false;
                messageSuccess.innerText = httpRequest.responseText;
            } else if (httpRequest.status === 500) {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = "An error occured on the server, please try again later";
            } else if (errorCodes.includes(httpRequest.status)) {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = httpRequest.responseText;
            } else {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = "Something went wrong.";
            }

            if (groupId == -1) {
                getGroup('teachers')
            } else {
                getGroup(groupId)
            }
            updateGroupList()
        }
    }

    httpRequest.open('POST', apiPrefix + `/groups/${groupId}/removeMembers`);
    let params = new FormData();
    document.querySelectorAll('.selected').forEach(row => {
        userIds.push(row.querySelector('.userId').textContent)
    })
    params.append('listOfUserIds', userIds.join(','));

    httpRequest.send(params);
}

/**
 * Reads the information from the event and sends the call to add the group to selected users
 * @param event Drop event
 * @param groupId Id of the group being dropped on
 */
function dropUsers(event, groupId) {
    event.preventDefault();
    if (groupId == 0) {
        if (event.target.closest(".card").querySelector("a").innerText == "Teaching Staff") {
            groupId = -1
        } else {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = "Cannot add users to Members without a group";
            return;
        }
    }
    let data = event.dataTransfer.getData("origin");
    if (data == "Members without a group") {
        addUsers(groupId, "unassigned")
    } else {
        addUsers(groupId, null)
    }
}

/**
 * Prepares the selected users when they start to get dragged
 * @param event Drag event
 */
function userDragStart(event) {
    if (!event.target.closest("tr").classList.contains("selected")) {
        selectUser(event)
    }
    event.dataTransfer.setData("origin", event.target.closest("#group-display").querySelector("h3").innerText);
}

/**
 * Provides a visual indicator that the user is over a valid drop location
 * @param event Drag event
 */
function allowDrop(event) {
    event.preventDefault();
}

/**
 * Makes a call to the server and adds the selected members to the group
 * @param groupId Id of the selected group
 */
function addUsers(groupId, originGroupId) {
    let httpRequest = new XMLHttpRequest();
    let userIds = []
    let errorCodes = [400, 403]
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                messageDanger.hidden = true;
                messageSuccess.hidden = false;
                messageSuccess.innerText = httpRequest.responseText;
            } else if (httpRequest.status === 500) {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = "An error occurred on the server, please try again later";
            } else if (errorCodes.includes(httpRequest.status)) {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = httpRequest.responseText;
            } else {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = "Something went wrong.";
            }
            if (originGroupId != null) {
                if (groupId == -1 && typeof originGroupId != 'string') {
                    getGroup('teachers')
                } else {
                    getGroup(originGroupId)
                }
            }
            updateGroupList()
        }
    }

    httpRequest.open('POST', apiPrefix + `/groups/${groupId}/addMembers`);
    let params = new FormData();
    document.querySelectorAll('.selected').forEach(row => {
        userIds.push(row.querySelector('.userId').textContent)
    })
    params.append('listOfUserIds', userIds.join(','));

    httpRequest.send(params);
}

/**
 * Connects to the websocket server
 */
function connect() {
    let websocketProtocol = window.location.protocol === 'http:' ? 'ws://' : 'wss://'
    let stompClient = new StompJs.Client({
        brokerURL: websocketProtocol + window.location.host + apiPrefix + '/lensfolio-websocket',
        debug: function (str) {
            // console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = function () {
        console.log('Active updating enabled');
        subscribe(stompClient)
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
function subscribe(stompClient) {
    stompClient.subscribe('/element/groups/', updateGroup);
    stompClient.subscribe(`/element/user/nameOnly`, updateUser);
}

/**
 * Updates a user's information if it has changed
 * @param message Message userId of changed user
 */
function updateUser(message) {
    let array = message.body.split(' ')
    let id = array[0]
    let userElement = document.getElementById(`user` + id + `Row`);
    if (userElement) {
        let httpRequest = new XMLHttpRequest();
        httpRequest.onreadystatechange = function () {
            if (httpRequest.readyState === XMLHttpRequest.DONE) {
                if (httpRequest.status === 200) {
                    userElement.innerHTML = httpRequest.responseText;
                } else if (httpRequest.status === 400) {
                    messageDanger.hidden = false;
                    messageSuccess.hidden = true;
                    messageDanger.innerText = "Bad Request";
                }
            }
        }

        httpRequest.open('GET', apiPrefix + `/groups/user/${id}`);
        httpRequest.send();
    }
}

/**
 * Replaces the relevant component of the sprint table
 * @param message Message with sprint and edit type
 */
function updateGroup(message) {
    let array = message.body.split(' ')
    let group = array[1]
    let component = array[2]
    let action = array[3]

    if (component == "members" || action === "edited") {
        if (document.getElementById("groupId").value == group) {
            getGroup(group)
        } else {
            getGroup(document.getElementById("groupId").value)
        }
        updateGroupList()
    } else if (action === "deleted") {
        if (document.getElementById("groupId").value == group) {
            getGroup("unassigned")
        } else {
            getGroup(document.getElementById("groupId").value)
        }
        updateGroupList()
    } else {
        console.log("Unknown command: " + action)
    }
}

/**
 * Runs the connect function when the document is loaded
 */
document.addEventListener('DOMContentLoaded', function () {
    connect();
})