function updateProjectDetails(projectId, projectName) {
    document.getElementById('message').innerText =  `Are you sure you want to delete ${projectName}`;
    document.getElementById('deleteProject').setAttribute('action', `/dashboard/deleteProject/${projectId}`);
}
