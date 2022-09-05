/**
 * Attempts to connect to the git repository using the details provided
 * @param event Form submit event
 */
async function connectTest(event) {
    event.preventDefault()

    let message = document.getElementById("connection-message")

    let projectId = document.getElementById("project-id").value
    let accessToken = document.getElementById("access-token").value
    let hostAddress = document.getElementById("host-address").value

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
}