function updateProjectDetails(projectId, projectName, apiPrefix) {
    document.getElementById('message').innerText =  `Are you sure you want to delete ${projectName}`;
    document.getElementById('deleteProject').setAttribute('action', `${apiPrefix}/dashboard/deleteProject/${projectId}`);
}
