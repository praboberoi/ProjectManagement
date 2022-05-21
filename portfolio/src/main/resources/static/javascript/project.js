function updateSprintDetails(sprintId, sprintName, projectId, prefix) {
    if (prefix === null)
        prefix = ""
    document.getElementById('message').innerText = `Are you sure you want to delete ${sprintName}`;
    document.getElementById('deleteSprint').setAttribute('action', `${prefix}/project/${projectId}/deleteSprint/${sprintId}`);
}