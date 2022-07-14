let page = 0;

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
};

function removeRole(role, userId) {

    httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                if (httpRequest.response != "") {
                    startDateError.innerText = httpRequest.response;
                    startDateElement.classList.add("formError");
                    endDateElement.classList.add("formError");
                }
                const roleElement = document.getElementById(`user${userId}Role${role}`)
                roleElement.remove()
                getUserDataTable(page)
            }
        }
    }

    httpRequest.open('DELETE', '/usersList/removeRole', true);
    httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    httpRequest.send("role=" + role + "&userId=" + userId);

}