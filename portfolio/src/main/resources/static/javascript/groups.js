/**
 * Check and update the length of characters of group short name.
 */
function checkShortName() {
    let groupShortNameElement = document.getElementById("group-short-name");

    let charMessage = document.getElementById("charCount");
    let charCount = groupShortNameElement.value.length;
    charMessage.innerText = charCount + ' '

}

/**
 * Check and update the length of characters of group long name.
 */
function checkLongName() {
    let groupLongNameElement = document.getElementById("group-long-name");

    let charMessage = document.getElementById("charCountLong");
    let charCount = groupLongNameElement.value.length;
    charMessage.innerText = charCount + ' '

}

/**
 * Makes a call to the server and replaces the current group list with the new one
 */
 function updateGroupList() {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
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
 * Makes a call to the server and replaces the current group with the new one
 */
function getSelectedGroup(selectedGroupId) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                document.getElementById("selectedGroup").outerHTML = httpRequest.responseText;
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

    httpRequest.open('GET', apiPrefix + `/groups/${selectedGroupId}`);
    httpRequest.send();
}

/**
 * Selects and highlights the group members selected. Functions with shift and control clicking.
 * @param event The click event on the group member
 */
function selectUser(event) {
    let removeUserButton = document.querySelector('#remove-users')
    if (removeUserButton != null) {
        removeUserButton.disabled = false
    }
    let userTable = document.querySelectorAll("#userListDataTable tr")

    if (event.shiftKey) {
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
        

    } else if (event.ctrlKey) {
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
    httpRequest.onreadystatechange = function (){
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
                getSelectedGroup('teachers')
            } else {
                getSelectedGroup(groupId)
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

function addUsers(groupId) {
    addUsersCall(groupId, groupId)
}

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
        addUsersCall(groupId, "unassigned")
    } else {
        addUsersCall(groupId, null)
    }
}

function userDragStart(event) {
    if (!event.target.closest("tr").classList.contains("selected")) {
        selectUser(event)
    }
    event.dataTransfer.setData("origin", event.target.closest("#group-display").querySelector("h5").innerText);
}

function allowDrop(event) {
    event.preventDefault();
}

/**
 * Makes a call to the server and adds the selected members to the group
 * @param groupId Id of the selected group
 */
 function addUsersCall(groupId, originGroupId) {
    let httpRequest = new XMLHttpRequest();
    let userIds = []
    let errorCodes = [400, 403]
    httpRequest.onreadystatechange = function (){
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
                    getSelectedGroup('teachers')
                } else {
                    getSelectedGroup(originGroupId)
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