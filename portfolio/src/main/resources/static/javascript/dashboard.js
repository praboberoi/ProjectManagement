/**
 * Initilises the delete modal for deleting a project
 * @param {int} projectId 
 */
function projectDeleteModalInit(projectId) {
    let projectName = document.getElementById(`project${projectId}-card`).getElementsByClassName('project-name')[0].text
    document.getElementById('messageProject').innerText =  `Are you sure you want to delete ${projectName}`;
    document.getElementById('confirmProjectDeleteBtn').setAttribute('onclick', `deleteProject(${projectId})`);
}

/**
 * Initilises the project modal for editing the selected project
 * @param {int} projectId 
 */
function editProjectModalInit(projectId) {
    document.getElementById('projectId').value = projectId

    let projectName = document.getElementById(`project${projectId}-card`).getElementsByClassName('project-name')[0].text
    document.getElementById('project-name').value =  projectName;
    checkProjectName()

    let projectStartDate = document.getElementById(`project${projectId}-startDate`).value
    document.getElementById('startDate').value =  projectStartDate;

    let projectEndDate = document.getElementById(`project${projectId}-endDate`).value
    document.getElementById('endDate').value =  projectEndDate;
    checkProjectDates()

    let projectDescription = document.getElementById(`project${projectId}-description`).innerText
    document.getElementById('projectDescription').value =  projectDescription;
    checkProjectDescription()

    document.getElementById('projectFormCreateBtn').hidden = true
    document.getElementById('projectFormEditBtn').hidden = false
}

/**
 * Initilises the project modal for editing the selected project
 */
 function createProjectModalInit() {
    document.getElementById('projectId').value = 0

    document.getElementById('project-name').value = "";
    checkProjectName()

    document.getElementById('startDate').value = new Date().toLocaleDateString("en-CA");

    let endDate = new Date();
    endDate.setMonth(endDate.getMonth() + 8);

    document.getElementById('endDate').value =  endDate.toLocaleDateString("en-CA");
    checkProjectDates()

    document.getElementById('projectDescription').value =  "";
    checkProjectDescription()

    document.getElementById('projectFormCreateBtn').hidden = false
    document.getElementById('projectFormEditBtn').hidden = true
}

/**
 * Sends a delete request to the server and updates the delete modal
 * @param {int} projectId 
 */
function deleteProject(projectId) {
    let httpRequest = new XMLHttpRequest();

    let modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('conformationModal'))
    let modalError = document.getElementById('projectDeleteModalError')

    httpRequest.onreadystatechange = updateModal(httpRequest, modal, modalError)

    httpRequest.open('DELETE', apiPrefix + `/project/${projectId}`);
    httpRequest.send();
}

/**
 * Connects to the websocket server
 */
 function connect() {
    let websocketProtocol = window.location.protocol === 'http:'?'ws://':'wss://'
    stompClient = new StompJs.Client({
        brokerURL: websocketProtocol + window.location.host + apiPrefix + '/lensfolio-websocket',
        debug: function(str) {
            // console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });
    
    stompClient.onConnect = function () {
        subscribe()
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
 function subscribe() {
    stompClient.subscribe('/element/project', handleProjectNotification);
}

/**
 * Handles project updates from the server
 * @param message Message with deadline and edit type
 */
 function handleProjectNotification(message) {
    let array = message.body.split(' ')
    let project = array[0]
    let action = array[1]

    let projectCard = document.getElementById(project + "-card");

    if (action === "edited") {
        loadProjects()
    } else if (action === "deleted" && projectCard) {
        projectCard.outerHTML = ""
        return
    } else if (action === "editing" && projectCard) {
        let user = array[2]
        document.getElementById(deadline + '-notification').innerText =`${user} is currently editing`
        document.getElementById(deadline + '-edit-btn').hidden = true
        document.getElementById(deadline + '-delete-btn').hidden = true
    } else if (action === "finished" && projectCard) {
        document.getElementById(deadline + '-notification').innerText = ""
        document.getElementById(deadline + '-edit-btn').hidden = false
        document.getElementById(deadline + '-delete-btn').hidden= false
    } else {
        console.log("Unknown event or command: " + deadline + " " + action)
    }
}

/**
 * Loads the list of projects
 */
 function loadProjects() {
    let httpRequest = new XMLHttpRequest();

    let eventElement = document.getElementById("project-list")
    httpRequest.open('GET', apiPrefix + `/projects`);
    httpRequest.onreadystatechange = () => updateElement(httpRequest, eventElement)

    httpRequest.send();
}

/**
 * Replaces the old http component with the new one contained in the request
 * @param httpRequest Request containing a model view element
 * @param element The element to replace
 * @param errorMessage Optional variable, changes the default error message location
 */
 function updateElement(httpRequest, element, errorMessage = messageDanger){
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        errorMessage.innerText = ""
        if (httpRequest.status === 200) {
            element.innerHTML = httpRequest.responseText;
        } else if (httpRequest.status === 400) {
            errorMessage.innerText = "Bad Request";
        } else if (httpRequest.status === 404) {
            errorMessage.innerText = "Unable to load " + element.id;
        } else {
            errorMessage.innerText = "Something went wrong.";
        }
    }
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

/**
 * Runs the connect function when the document is loaded
 */
 document.addEventListener('DOMContentLoaded', function() {
    connect();
})