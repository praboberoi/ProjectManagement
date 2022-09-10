var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
  return new bootstrap.Tooltip(tooltipTriggerEl)
})

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

    const response = await fetch(hostAddress + "/api/v4/projects/" + projectId, {
        method: 'GET',
        headers: {
            'PRIVATE-TOKEN': accessToken, //'sVMvHmHxhJeqdZBBchDB' <-- This is a project token for an empty gitlab repo (id = 13964) that I have created for testing purposes
            'Content-Type': 'application/json',
        },
    });

    const repo = await response.json();
    if (!repo.hasOwnProperty('id')) {
        message.innerText = "Repo not found"
        return
    }
    message.innerText = "Connected to repo: " + repo.name
    getRecentActions()
}

async function getRecentActions() {
    let message = document.getElementById("connection-message")

    if (message.innerText === "Repo not found") {
        return
    }

    let projectId = document.getElementById("git-project-id").value
    let accessToken = document.getElementById("git-access-token").value
    let hostAddress = document.getElementById("git-host-address").value

    const response = await fetch(hostAddress + "/api/v4/projects/" + projectId + "/events", {
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

        // Image component
        var userImageContainer = document.createElement('div');
        eventBody.appendChild(userImageContainer)

        // Profile photo
        var userImage = document.createElement('img');
        userImageContainer.appendChild(userImage)
        userImage.src = event.author.avatar_url
        userImage.height = 20
        userImage.width = 20
        userImage.classList.add('d-inline-block', 'align-text-top', 'profile-photo')

        // Username
        userImageContainer.insertAdjacentText('beforeend', " " + event.author.name)

        // Action component
        var actionContainer = document.createElement('div');
        eventBody.appendChild(actionContainer)

        // Action
        var action = document.createElement('p');
        actionContainer.appendChild(action)
        action.innerText = event.action_name

        // Time component
        var timeContainer = document.createElement('div');
        eventBody.appendChild(timeContainer)
        timeContainer.innerText = new Date(event.created_at).toLocaleDateString("en-GB", { year: 'numeric', month: 'long', day: 'numeric' })
    });

    console.log(events)
}