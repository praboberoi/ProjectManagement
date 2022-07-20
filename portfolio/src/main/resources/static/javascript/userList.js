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

/**
 * Posts the new role for the user to have deleted from the server. Once 
 * completed, reloads user table.
 * @param {UserRole} role 
 * @param {int} userId 
 */
function removeRole(role, userId) {
    httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
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
            const pageMessage = document.getElementById("pageMessage");
            if (httpRequest.status === 200) {
                const userRole = document.getElementById(`user${userId}Row`);
                userRole.innerHTML = httpRequest.responseText;
                pageMessage.innerHTML = "<div>&nbsp;</div>";
            } else {
                pageMessage.innerHTML = httpRequest.responseText;
            }
        }
    }

    httpRequest.open('POST', 'user/' + userId + '/addRole', true);
    httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    httpRequest.send("role=" + role);
}