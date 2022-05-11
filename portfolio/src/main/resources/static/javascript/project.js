function updateSprintDetails(sprintId, sprintName, projectId, prefix) {
    document.getElementById('message').innerText =  `Are you sure you want to delete ${sprintName}`;
    document.getElementById('deleteSprint').setAttribute('action', `${prefix}/${projectId}/deleteSprint/${sprintId}`);
}