function updateSprintDetails(sprintId, sprintName, projectId) {
    document.getElementById('message').innerText =  `Are you sure you want to delete ${sprintName}`;
    document.getElementById('deleteSprint').setAttribute('action', `${projectId}/deleteSprint/${sprintId}`);
}