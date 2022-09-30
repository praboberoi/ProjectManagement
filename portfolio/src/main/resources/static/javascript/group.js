let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
let filterByUser = ""
let filterByActionType = ""
let projectIdValidate = /^\d+$/;
let jsonRepo;
let currentPage = 0;
let totalPages = 0;
// Regular expression for Group Name field. No leading white spaces or empty field.
const groupNameRegex = /^\S/
let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
})

/**
 * Count down the characters remaining in the Group Short name, and check the length is between 3 and 50 characters.
 */
function checkShortName(event) {
    let groupShortNameElement = event.target;
    let groupShortNameErrorElement = groupShortNameElement.parentNode.querySelector('#shortNameError')
    let charMessage = groupShortNameElement.parentNode.querySelector("#charCount");
    let charCount = groupShortNameElement.value.length;
    charMessage.innerText = charCount + ' '
    if (charCount < 3 || charCount > 50) {
        groupShortNameElement.classList.add('formError');
        groupShortNameErrorElement.innerText = "Group short name must be between 3 and 50 characters."
        groupShortNameElement.setCustomValidity("Invalid Field")
    } else if (groupShortNameElement.value.toLowerCase() == "teaching staff" || groupShortNameElement.value.toLowerCase() == "members without a group"){
        groupShortNameElement.classList.add('formError');
        groupShortNameErrorElement.innerText = "Group short cannot be the same as a system group name."
        groupShortNameElement.setCustomValidity("Invalid Field")
    } else if (! groupNameRegex.test(groupShortNameElement.value)) {
        groupShortNameElement.classList.add("formError");
        groupShortNameErrorElement.innerText = "Group short name must not start with space characters";
        groupShortNameElement.setCustomValidity("Invalid Field")
    } else {
        groupShortNameElement.classList.remove("formError");
        groupShortNameErrorElement.innerText = null;
        groupShortNameElement.setCustomValidity("");
    }
}

/**
 * Count down the characters remaining in the Group Long name, and check the length is between 3 and 100 characters.
 */
function checkLongName(event) {
    let groupLongNameElement = event.target;
    let groupLongNameErrorElement = groupLongNameElement.parentNode.querySelector("#longNameError")
    let charMessage = groupLongNameElement.parentNode.querySelector("#charCountLong");
    let charCount = groupLongNameElement.value.length;
    if (charCount < 3 || charCount > 100) {
        groupLongNameElement.classList.add('formError');
        groupLongNameErrorElement.innerText = "Group long name must be between 3 and 100 characters."
        groupLongNameElement.setCustomValidity("Invalid Field")
    } else if (! groupNameRegex.test(groupLongNameElement.value)) {
        groupLongNameElement.classList.add("formError");
        groupLongNameErrorElement.innerText = "Group long name must not start with space characters";
        groupLongNameElement.setCustomValidity("Invalid Field")
    } else {
        groupLongNameElement.classList.remove("formError");
        groupLongNameErrorElement.innerText = null;
        groupLongNameElement.setCustomValidity("");
    }
    charMessage.innerText = charCount + ' '

}

/**
 * Sends a request to the server to save the group
 * @param event Form submit request
 */
function saveGroup() {
        let httpRequest = new XMLHttpRequest();

        let editModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('editModal'))
        let modalError = document.getElementById('editModalError')

        httpRequest.onreadystatechange = () => updateModal(httpRequest, editModal, modalError)

        httpRequest.open('POST', apiPrefix + `/groups`);

        let formData = new FormData(document.forms.editGroupForm)

        httpRequest.send(formData);
}

/**
 * Updates the error message and removes the modal if there is no issues
 * @param httpRequest Request made to the server
 * @param modal Which modal is being edited
 * @param modalError Error message div that displays an error
 */
 function updateModal(httpRequest, modal, modalError) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            modalError.innerText = ""
            messageSuccess.innerText = httpRequest.responseText;
            modal.hide()
        } else if (httpRequest.status === 500) {
            messageSuccess.innerText = ""
            modalError.innerText = "An error occurred on the server, please try again later";
        } else if (httpRequest.status == 400) {
            messageSuccess.innerText = ""
            modalError.innerText = httpRequest.responseText;
        } else {
            messageSuccess.innerText = ""
            modalError.innerText = "Something went wrong.";
        }
    }
}

function updateTitle() {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                document.getElementById("title").outerHTML = httpRequest.responseText;
            } else if (httpRequest.status === 400) {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = "Bad Request";
            }
        }
    }

    httpRequest.open('GET', apiPrefix + `/group/${groupId}/title`);
    httpRequest.send();
}

/**
 * Checks to see if a string is a valid url
 * @param string String to be validated
 * @returns {boolean} True if the string is a valid URL, false otherwise
 */
function isValidHttpUrl(string) {
    let url;
    try {
        url = new URL(string)
    } catch (_) {
        return false;
    }
    return url.protocol === "http:" || url.protocol === "https:"
}

const GIT_API = "/api/v4/"

/**
 * Toggles the visibility of the recent actions component
 */
function toggleRecentActions() {
    document.getElementById("recent-actions-container").classList.toggle('d-lg-none')
    document.getElementById("main-content").classList.toggle('col-lg-12')
    document.getElementById("actions-toggle-tab").classList.toggle('active')
}

/**
 * Attempts to connect to the git repository using the details provided
 * @param saving
 */
function connectToRepo(saving = false) {
    let httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status !== 200) {
                clearRecentActions()
                return
            }

            let repoName = document.getElementById("git-project-name")

            jsonRepo = JSON.parse(httpRequest.response)

            fetch(jsonRepo.hostAddress + GIT_API + "projects/" + jsonRepo.gitlabProjectId, {
                method: 'GET',
                headers: {
                    'PRIVATE-TOKEN': jsonRepo.accessToken, //'sVMvHmHxhJeqdZBBchDB' <-- This is a project token for an empty gitlab repo (id = 13964) that I have created for testing purposes
                    'Content-Type': 'application/json',
                },
            }).then(async (response) => {
                const repo = await response.json();
                if (saving) {
                    if (!repo.hasOwnProperty('id')) {
                        document.getElementById("messageDanger").innerText = "Repo not found"
                        clearRecentActions()
                        return
                    }


                    document.getElementById("messageSuccess").innerText = "Connected to repo: " + repo.name
                } else {
                    if (!repo.hasOwnProperty('id')) {
                        clearRecentActions()
                        return
                    }
                }

                if (repoName !== undefined) {
                    repoName.value = repo.name
                }

                getRecentActions(jsonRepo)
            }).catch((error) => {
                if (repoName !== undefined) {
                    repoName.value = ""
                }
                if (saving) {
                    document.getElementById("messageDanger").innerText = "Error connecting to repository"
                }
                clearRecentActions()
            });
        }
    }

    httpRequest.open('GET', apiPrefix + `/repo/${groupId}`);

    httpRequest.send();
}

/**
 * Clears the recent actions component and replaces it with an error message
 */
function clearRecentActions() {
    let recentActions = document.getElementById("recent-action-cards")
    recentActions.innerHTML = ""
    let eventCard = document.createElement('div');
    recentActions.appendChild(eventCard);
    eventCard.classList.add('card', 'shadow-sm', 'bg-white', 'rounded', 'event-card', 'm-2', 'error-msg')
    eventCard.textContent = "Invalid repo, unable to retrieve recent actions"
}

/**
 * Calls the git api to get events from the project that has been provided. Formats these into cards for the recent actions component
 */
async function getRecentActions(repo) {
    const response = await fetch(repo.hostAddress + GIT_API + "projects/" + repo.gitlabProjectId + "/events", {
        method: 'GET',
        headers: {
            'PRIVATE-TOKEN': repo.accessToken, //'sVMvHmHxhJeqdZBBchDB' <-- This is a project token for an empty gitlab repo (id = 13964) that I have created for testing purposes
            'Content-Type': 'application/json',
        },
    });

    let events = await response.json();

    let recentActions = document.getElementById("recent-action-cards")

    if (events == undefined) {
        clearRecentActions()
        return
    }

    updateFilters(events);
    recentActions.innerHTML = ""
    if (filterByUser.length > 0)
        events = events.filter(event => event.author_username === filterByUser)

    if (filterByActionType.length > 0)
        events = events.filter(event => event.action_name === filterByActionType)

    if(currentPage > 0 && currentPage * 5 > events.length)
        currentPage = 0

    updatePages(events);

    events = events.slice(currentPage * 4, (currentPage * 4) + 4)


    events.forEach( (event) => {

        // Card
        let eventCard = document.createElement('div');
        recentActions.appendChild(eventCard);
        eventCard.classList.add('card', 'shadow-sm', 'bg-white', 'rounded', 'event-card', 'm-2')

        // Card container
        let eventBody = document.createElement('div');
        eventCard.appendChild(eventBody)
        eventBody.classList.add('d-flex', 'justify-content-between', 'py-2')

        let dataContainer = document.createElement('div');
        eventBody.appendChild(dataContainer)

        addUserProfile(dataContainer, event)

        // Action component
        let actionContainer = document.createElement('div');
        eventBody.appendChild(actionContainer)
        actionContainer.classList.add('text-end')

        addDate(dataContainer, event)

        let action = document.createElement('p');
        actionContainer.appendChild(action)

        if (event.action_name == 'joined') {
            action.innerText = "Joined the project"
        } else if (event.action_name == "pushed new") {
            action.innerText = "Created branch: " + event.push_data.ref
        } else if (event.action_name == "pushed to") {
            action.innerText = "Pushed " + event.push_data.commit_count + " commits to " + event.push_data.ref
        } else if (event.action_name == "created") {
            addCreated(actionContainer, event)
        } else if (event.action_name == "opened") {
            action.innerText = "Opened new " + event.target_type.replace(/([A-Z])/g, ' $1') + ": " + event.target_title
        } else if (event.action_name == "commented on") {
            action.innerText = "Commented on " + event.note.noteable_type.replace(/([A-Z])/g, ' $1') + ": " + event.target_title
        } else if (event.action_name == "approved") {
            action.innerText = "Approved " + event.target_type.replace(/([A-Z])/g, ' $1') + ": " + event.target_title
        } else if (event.action_name == "accepted") {
            action.innerText = "Merged " + event.target_type.replace(/([A-Z])/g, ' $1') + ": " + event.target_title
        } else if (event.action_name == "closed") {
            action.innerText = "Closed " + event.target_type.replace(/([A-Z])/g, ' $1') + ": " + event.target_title
        } else {
            action.innerText = "Performed a " + event.action_name
        }
    });
}

/**
 * Adds the user profile component to the provided element
 * @param element HTML element to append the profile to
 * @param event GitLab api response
 */
function addUserProfile(element, event) {
    // Image component
    let userImageContainer = document.createElement('div');
    element.appendChild(userImageContainer)

    // Profile photo
    let userImage = document.createElement('img');
    userImageContainer.appendChild(userImage)
    userImage.src = event.author.avatar_url
    userImage.height = 20
    userImage.width = 20
    userImage.classList.add('d-inline-block', 'align-text-top', 'profile-photo')

    // Username
    userImageContainer.insertAdjacentText('beforeend', " " + event.author_username)
}

/**
 * Front end validation for the projectAlias element
 */
function validateProjectAlias() {
    let projectAliasElement = document.getElementById("git-project-alias");
    let projectAliasErrorElement = document.getElementById("gitProjectAliasError");

    if (projectAliasElement.value.length < 1 || projectAliasElement.value.length > 50) {
        projectAliasElement.classList.add("formError")
        projectAliasErrorElement.innerText = "Project Alias field must be between 1 and 50 characters"
        projectAliasElement.setCustomValidity("Invalid field.")
    } else {
        projectAliasElement.classList.remove("formError");
        projectAliasErrorElement.innerText = null;
        projectAliasElement.setCustomValidity("");
    }
}


/**
 * Front end validation for the projectHostAddress element
 */
function validateProjectHostAddress() {
    let projectHostAddressElement = document.getElementById("git-host-address");
    let projectHostAddressErrorElement = document.getElementById("gitHostAddressError");

    if (projectHostAddressElement.value.length < 1) {
        projectHostAddressElement.classList.add("formError")
        projectHostAddressErrorElement.innerText = "Project host address field must not be empty"
        projectHostAddressElement.setCustomValidity("Invalid field.")
    } else if (!isValidHttpUrl(projectHostAddressElement.value)) {
        projectHostAddressElement.classList.add("formError")
        projectHostAddressErrorElement.innerText = "Project host address must be a valid HTTP URL"
        projectHostAddressElement.setCustomValidity("Invalid field.")
    } else {
        projectHostAddressElement.classList.remove("formError");
        projectHostAddressErrorElement.innerText = null;
        projectHostAddressElement.setCustomValidity("");
    }
}

/**
 * Front end validation for the projectID element
 */
function validateProjectID() {
    let projectIDElement = document.getElementById("git-project-id");
    let projectIDErrorElement = document.getElementById("gitProjectIdError");

    if (projectIDElement.value.length < 1 || projectIDElement.value.length > 50) {
        projectIDElement.classList.add("formError")
        projectIDErrorElement.innerText = "Project ID field must be between 1 and 50 characters"
        projectIDElement.setCustomValidity("Invalid field.")
    } else if (!projectIdValidate.test(projectIDElement.value)) {
        projectIDElement.classList.add("formError")
        projectIDErrorElement.innerText = "Project ID field can only contain numbers"
        projectIDElement.setCustomValidity("Invalid field.")
    } else {
        projectIDElement.classList.remove("formError");
        projectIDErrorElement.innerText = null;
        projectIDElement.setCustomValidity("");
    }
}


/**
 * Adds the date component to the provided element
 * @param element HTML element to append the date to
 * @param event GitLab api response
 */
function addDate(element, event) {
    let timeContainer = document.createElement('div');
    element.appendChild(timeContainer)
    timeContainer.innerText = new Date(event.created_at).toLocaleDateString("en-GB", { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

/**
 * Add the text for the relevant create event
 * @param element HTML element to add the text to
 * @param event GitLab api response
 */
function addCreated(element, event) {
    let action = document.createElement('p');
    element.appendChild(action)
    if (event.target_type == "WikiPage::Meta") {
        action.innerText = "Created wiki page: " + event.target_title
    } else if (event.target_type == null) {
        action.innerText = "Created the project"
    }
}

function saveRepoSettings(event) {
    if (event != null) {
        event.preventDefault()
    }

    let httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = () => {
        processAction(httpRequest)
        connectToRepo(true)
    }

    httpRequest.open('POST', apiPrefix + `/repo/${groupId}/save`);

    let formData = new FormData(document.forms.repoSettingsForm)

    httpRequest.send(formData);



}

/**
 * Replaces the old messages with the new one contained in the request
 * @param httpRequest Request containing a model view element
 */
function processAction(httpRequest) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            messageSuccess.hidden = false
            messageDanger.hidden = true;
            messageSuccess.innerText = httpRequest.responseText;
        } else if (httpRequest.status === 400) {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = httpRequest.responseText;
        } else {
            messageDanger.hidden = false;
            messageSuccess.hidden = true;
            messageDanger.innerText = "Something went wrong.";
        }
    }
}

/**
 * Runs when the page is loaded
 */
document.addEventListener('DOMContentLoaded', function () {
    if (document.getElementById('recent-actions-container') != undefined) {
        connectToRepo()
    }
});

/**
 * Updates the list of all the filters available for selection based on the events list passed
 */
function updateFilters(events) {
    let userFilter = document.getElementById('userFilter')
    let actionType = document.getElementById('actionType')

    const userSet = new Set();
    const actionTypeSet = new Set();

    userFilter.innerText = "";
    actionType.innerText = "";

    events.forEach(event => {
        userSet.add(event.author_username);
        actionTypeSet.add(event.action_name);
    })

    if (filterByUser.length === 0) {
        userFilter.innerHTML = `<option selected>Filter By User</option>`
        userSet.forEach(user => userFilter.innerHTML += `<option>${user}</option>`)

    } else {
        userFilter.innerHTML += `<option>Clear Filter</option>`
        userSet.forEach(user => userFilter.innerHTML += user === filterByUser ? `<option selected>${user}</option>` : `<option>${user}</option>`)
    }

    if (filterByActionType.length === 0) {
        actionType.innerHTML = `<option selected>Filter By Action Type</option>`
        actionTypeSet.forEach(action => actionType.innerHTML += `<option>${action}</option>`)

    } else {
        actionType.innerHTML += `<option>Clear Filter</option>`
        actionTypeSet.forEach(action => actionType.innerHTML +=
            action === filterByActionType ? `<option selected>${action}</option>` : `<option>${action}</option>`)


    }

}

if (document.getElementById('recent-actions-container') != undefined) {
    /**
     * Event listener for change in the userFilter
     */
    document.getElementById('userFilter').addEventListener('change', function () {
        if (this.value === 'Clear Filter') {
            filterByUser = ""
        } else {
            filterByUser = this.value
        }

        getRecentActions(jsonRepo)

    })
    /**
     * Event listener for change in the actionTypeFilter
     */
    document.getElementById('actionType').addEventListener('change', function () {
        if (this.value === 'Clear Filter') {
            filterByActionType = ""
        } else{
            filterByActionType = this.value
        }

        getRecentActions(jsonRepo)

    })
}



/**
 * Connects to the websocket server
 */
 function connect() {
    let websocketProtocol = window.location.protocol === 'http:' ? 'ws://' : 'wss://'
    let stompClient = new StompJs.Client({
        brokerURL: websocketProtocol + window.location.host + apiPrefix + '/lensfolio-websocket',
        debug: function (str) {
            // console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = function () {
        console.log('Active updating enabled');
        subscribe(stompClient)
        document.getElementById("websocket-status").value = "connected"
    };

    stompClient.onStompError = function () {
        console.log('Websocket communication error')
    }

    stompClient.activate();
}

/**
 * Subscribes to the required websocket notification channels
 */
function subscribe(stompClient) {
    stompClient.subscribe(`/element/group/${groupId}`, updateGroup);
    stompClient.subscribe(`/element/user/`, updateUser);
}

/**
 * Replaces the group's title information
 * @param message Message with sprint and edit type
 */
 function updateGroup(message) {
    let array = message.body.split(' ')
    let component = array[0]
    let action = array[1]

    if (component == "details" || action === "edited") {
        updateTitle()
    } else {
        console.log("Unknown command: " + action)
    }
}

/**
 * Updates a user's information if it has changed
 * @param message Message userId of changed user
 */
function updateUser(message) {
    let array = message.body.split(' ')
    let id = array[0]
    let userElement = document.getElementById(`user` + id + `Row`);
    if (userElement) {
        let httpRequest = new XMLHttpRequest();
        httpRequest.onreadystatechange = function () {
            if (httpRequest.readyState === XMLHttpRequest.DONE) {
                if (httpRequest.status === 200) {
                    userElement.innerHTML = httpRequest.responseText;
                } else if (httpRequest.status === 400) {
                    messageDanger.hidden = false;
                    messageSuccess.hidden = true;
                    messageDanger.innerText = "Bad Request";
                }
            }
        }

        httpRequest.open('GET', apiPrefix + `/group/user/${id}`);
        httpRequest.send();
    }
}

function updateTitle() {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                document.getElementById("title").outerHTML = httpRequest.responseText;
            } else if (httpRequest.status === 400) {
                messageDanger.hidden = false;
                messageSuccess.hidden = true;
                messageDanger.innerText = "Bad Request";
            }
        }
    }

    httpRequest.open('GET', apiPrefix + `/group/${groupId}/title`);
    httpRequest.send();
}

/**
 * Runs the connect function when the document is loaded
 */
 document.addEventListener('DOMContentLoaded', function () {
    connect();
})



/**
 * Creates pagination based on the total number of pages
 * @param events
 */
function updatePages(events) {
     totalPages = Math.ceil(events.length / 4)

     const paginationDiv = document.getElementById('actionPagination');
     paginationDiv.innerHTML =
        `<nav aria-label="Page navigation">
            <ul class="pagination">
            <li class="page-item ${(totalPages ===  1 || currentPage === 0) ? 'disabled' : ''}">
                    <a class="page-link" aria-label="Last" onclick="updateCurrentPage(${0})">
                        <span aria-hidden="true">&laquo;</span>
                        <span class="sr-only">First</span>
                  </a>
                </li>
                <li class="page-item ${currentPage === 0 ? 'disabled' : ''}" id="previousPage">
                    <a class="page-link" aria-label="Previous" onclick="updateCurrentPage(${currentPage - 1})">
                        <span aria-hidden="true">&lsaquo;</span>
                        <span class="sr-only">Previous</span>
                    </a>
                </li>
                <li class="page-item active" id="page-${currentPage + 1}">
                     <a class="page-link" onclick="updateCurrentPage(${currentPage})">${currentPage + 1}/${totalPages}</a></li>
                <li class="page-item" id="page-${currentPage + 2}">
                
                <li class="page-item ${(totalPages ===  1 || currentPage + 1 === totalPages) ? 'disabled' : ''}" id="nextPage">
                    <a class="page-link" aria-label="Next" onclick="updateCurrentPage(${currentPage + 1})">
                        <span aria-hidden="true">&rsaquo;</span>
                        <span class="sr-only">Next</span>
                    </a>
                </li>
                <li class="page-item ${(totalPages ===  1 || currentPage + 1 === totalPages) ? 'disabled' : ''}">
                    <a class="page-link" aria-label="Last" onclick="updateCurrentPage(${totalPages - 1})">
                        <span aria-hidden="true">&raquo;</span>
                        <span class="sr-only">Last</span>
                  </a>
                </li>
            </ul>
        </nav>`
 }

/**
 * updates the current page with the give page number if it is in the range of
 * @param newPage the page to be updated to
 */
function updateCurrentPage(newPage) {
     if (newPage >= 0 && newPage <= totalPages) {
         currentPage = newPage
         getRecentActions(jsonRepo).then()
     }
 }
