let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
let projectIdValidate = /^\d+$/;
let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
  return new bootstrap.Tooltip(tooltipTriggerEl)
})

function isValidHttpUrl(string) {
    let url;
    try {
        url = new URL(string)
    } catch (_) {
        return false;
    }
    return url.protocol === "http:" || url.protocol === "https:"
}

/**
 * Attempts to connect to the git repository using the details provided
 * @param event Form submit event
 */
function connectTest(event) {
    event.preventDefault()

    let message = document.getElementById("connection-message")

    let projectId = document.getElementById("git-project-id").value
    let accessToken = document.getElementById("git-access-token").value
    let hostAddress = document.getElementById("git-host-address").value

    fetch(hostAddress + "/api/v4/projects/" + projectId, {
        method: 'GET',
        headers: {
            'PRIVATE-TOKEN': accessToken, //'sVMvHmHxhJeqdZBBchDB' <-- This is a project token for an empty gitlab repo (id = 13964) that I have created for testing purposes
            'Content-Type': 'application/json',
        },
    }).then(async (response) => {
        const repo = await  response.json();
        if (!repo.hasOwnProperty('id')) {
            message.innerText = "Repo not found"
            return
        }
        document.getElementById("git-project-name").value = repo.name
        message.innerText = "Connected to repo: " + repo.name
    }).catch((error) => {
        document.getElementById("git-project-name").value = ""
        message.innerText = "Error connecting to repository"
    });
}

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