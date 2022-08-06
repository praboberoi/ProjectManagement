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
 * Makes a call to the server and replaces the current user data table with the new one
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