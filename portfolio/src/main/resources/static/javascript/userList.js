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