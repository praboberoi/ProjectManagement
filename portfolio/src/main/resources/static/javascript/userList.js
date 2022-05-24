let page = 0;
let retrieving = false;

function nextPage() {
    page += 1
    getUserDataTable();
}

function prevPage() {
    if (page <= 0) {
        return;
    }
    page -= 1
    getUserDataTable();
}

function getUserDataTable() {
    
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function (){
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.responseText.includes("<tr")) {
                document.getElementById("userListDataTable").innerHTML = httpRequest.responseText;
            } else if (page < 0){
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