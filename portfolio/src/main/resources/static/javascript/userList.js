let page = 0;
let retrieving = false;

/**
 * Increment page count and load the new data
 * @returns 
 */
function nextPage() {
    page += 1
    getUserDataTable();
}

/**
 * Decrement page count and load the new data
 * @returns 
 */
function prevPage() {
    if (page <= 0) {
        return;
    }
    page -= 1
    getUserDataTable();
}

/**
 * Makes a call to the server and replaces the current user data table with the new one
 */
function getUserDataTable() {
    
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.responseText.includes("<tr")) {
                document.getElementById("userListDataTable").innerHTML = httpRequest.responseText;
            } else if (page <= 0){
                page = 0;
            }else {
                page -= 1;
            }
        }
        retrieving = false;
    }

    httpRequest.open('GET', apiPrefix + '/usersList?page=' + page + '&limit=' + 5);
    retrieving = true;
    httpRequest.send();
};