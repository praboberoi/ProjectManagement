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
    console.log(repo)
    if (!repo.hasOwnProperty('id')) {
        message.innerText = "Repo not found"
        return
    }
    message.innerText = "Connected to repo: " + repo.name
}