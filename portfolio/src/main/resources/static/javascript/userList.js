let page = 0;
let sortField = 'firstName';
let isAsc = true;

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
    if (sortField == field) {
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
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                document.getElementById("userListDataTable").innerHTML = httpRequest.responseText;
    
                page = newPage;
            }
        }
    }

    httpRequest.open('GET', apiPrefix + '/usersList?page=' + newPage + '&limit=' + 10 + '&order=' + sortField + '&asc=' + (isAsc?0:1));
    retrieving = true;
    httpRequest.send();
};