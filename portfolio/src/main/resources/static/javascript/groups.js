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
            }
        }
    }

    httpRequest.open('GET', apiPrefix + `/groups/${selectedGroupId}`);
    httpRequest.send();
}

/**
 * Calls the server to delete the selected group and show the unassgned members group on success
 * @param groupId Id of the group to delete
 */
function deleteGroup(groupId) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                messageSuccess.hidden = false
                messageDanger.hidden = true;
                messageSuccess.innerText = httpRequest.responseText;
                getSelectedGroup("unassigned")
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