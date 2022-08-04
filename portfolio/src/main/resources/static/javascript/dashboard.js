function updateProjectDetails(projectId, projectName, apiPrefix) {
    if(apiPrefix === null)
        apiPrefix = ""
    document.getElementById('messageProject').innerText =  `Are you sure you want to delete ${projectName}`;
    document.getElementById('deleteProject').setAttribute('action', `${apiPrefix}/dashboard/deleteProject/${projectId}`);
}
