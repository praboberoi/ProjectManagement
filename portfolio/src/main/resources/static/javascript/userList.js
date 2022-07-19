let page = 0;
let messageDanger = document.getElementById("messageDanger");
let messageSuccess = document.getElementById("messageSuccess");

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
 * Makes a call to the server and replaces the current user data table with the new one
 */
function getUserDataTable(newPage) {
    messageDanger.hidden = true;
    messageSuccess.hidden = true;
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            document.getElementById("userListDataTable").innerHTML = httpRequest.responseText;

            page = newPage;
        }
    }

    httpRequest.open('GET', apiPrefix + '/usersList?page=' + newPage + '&limit=' + 10);
    retrieving = true;
    httpRequest.send();
    console.log("this is running")
};

function removeRole(role, userId) {

    httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function (qualifiedName, value) {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                const roleElement = document.getElementById(`user${userId}Role${role}`)
                roleElement.remove()
                messageDanger.hidden = true;

                messageSuccess.hidden = false;
                messageSuccess.innerText = httpRequest.response

            } else {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = httpRequest.response;
            }
        }
    }

    httpRequest.open('DELETE', '/usersList/removeRole', true);
    httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    httpRequest.send("role=" + role + "&userId=" + userId);

}