var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
  return new bootstrap.Tooltip(tooltipTriggerEl)
})

const GIT_API = "/api/v4/"

/**
 * Attempts to connect to the git repository using the details provided
 * @param event Form submit event
 */
async function connectTest(event) {
    event.preventDefault()

    let message = document.getElementById("connection-message")

    let projectId = document.getElementById("git-project-id").value
    let accessToken = document.getElementById("git-access-token").value
    let hostAddress = document.getElementById("git-host-address").value

    const response = await fetch(hostAddress + GIT_API + "projects/" + projectId, {
        method: 'GET',
        headers: {
            'PRIVATE-TOKEN': accessToken, //'sVMvHmHxhJeqdZBBchDB' <-- This is a project token for an empty gitlab repo (id = 13964) that I have created for testing purposes
            'Content-Type': 'application/json',
        },
    });

    const repo = await response.json();
    if (!repo.hasOwnProperty('id')) {
        message.innerText = "Repo not found"
        clearRecentActions()
        return
    }
    message.innerText = "Connected to repo: " + repo.name
    getRecentActions()
}

function clearRecentActions() {
    let recentActions = document.getElementById("recent-action-cards")
    recentActions.innerHTML = ""
    var eventCard = document.createElement('div');
    recentActions.appendChild(eventCard);
    eventCard.classList.add('card', 'shadow-sm', 'bg-white', 'rounded', 'event-card', 'm-2', 'error-msg')
    eventCard.textContent = "Invalid repo, unable to retrieve recent actions"
}

async function getRecentActions() {
    let message = document.getElementById("connection-message")

    if (message.innerText === "Repo not found") {
        return
    }

    let projectId = document.getElementById("git-project-id").value
    let accessToken = document.getElementById("git-access-token").value
    let hostAddress = document.getElementById("git-host-address").value

    const response = await fetch(hostAddress + GIT_API + "projects/" + projectId + "/events", {
        method: 'GET',
        headers: {
            'PRIVATE-TOKEN': accessToken, //'sVMvHmHxhJeqdZBBchDB' <-- This is a project token for an empty gitlab repo (id = 13964) that I have created for testing purposes
            'Content-Type': 'application/json',
        },
    });

    const events = await response.json();
    let recentActions = document.getElementById("recent-action-cards")
    recentActions.innerHTML = ""
    events.forEach(event => {
        // Card
        var eventCard = document.createElement('div');
        recentActions.appendChild(eventCard);
        eventCard.classList.add('card', 'shadow-sm', 'bg-white', 'rounded', 'event-card', 'm-2')

        // Card container
        var eventBody = document.createElement('div');
        eventCard.appendChild(eventBody)
        eventBody.classList.add('d-flex', 'justify-content-between', 'py-2')

        let dataContainer = document.createElement('div');
        eventBody.appendChild(dataContainer)

        addUserProfile(dataContainer, event)

        // Action component
        var actionContainer = document.createElement('div');
        eventBody.appendChild(actionContainer)
        actionContainer.classList.add('text-end')

        addDate(dataContainer, event)

        var action = document.createElement('p');
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
        }
    });

    console.log(events)
}


function addUserProfile(element, event) {
    // Image component
    var userImageContainer = document.createElement('div');
    element.appendChild(userImageContainer)

    // Profile photo
    var userImage = document.createElement('img');
    userImageContainer.appendChild(userImage)
    userImage.src = event.author.avatar_url
    userImage.height = 20
    userImage.width = 20
    userImage.classList.add('d-inline-block', 'align-text-top', 'profile-photo')

    // Username
    userImageContainer.insertAdjacentText('beforeend', " " + event.author_username)
}

function addDate(element, event) {
    var timeContainer = document.createElement('div');
    element.appendChild(timeContainer)
    timeContainer.innerText = new Date(event.created_at).toLocaleDateString("en-GB", { year: 'numeric', month: 'long', day: 'numeric' })
}

function addCreated(element, event) {
    var action = document.createElement('p');
    element.appendChild(action)
    if (event.target_type == "WikiPage::Meta") {
        action.innerText = "Created wiki page: " + event.target_title
    } else if (event.target_type == null) {
        action.innerText = "Created the project"
    }
}
