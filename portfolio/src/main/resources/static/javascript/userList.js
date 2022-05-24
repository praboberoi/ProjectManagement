let page = 0;
let retrieving = false;

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
            if (httpRequest.responseText.includes("<tr")) {
                console.log(newPage)
                document.getElementById("userListDataTable").innerHTML = httpRequest.responseText;

                document.querySelector(".pagination > li.active") .classList.remove("active")
                document.evaluate("//ul[contains(@class, 'pagination')]/li[.//a[text() = '" + (newPage + 1) + "']]", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE).singleNodeValue.classList.add("active");
                page = newPage;
            }
        }
        retrieving = false;
    }

    httpRequest.open('GET', apiPrefix + '/usersList?page=' + newPage + '&limit=' + 5);
    retrieving = true;
    httpRequest.send();
};