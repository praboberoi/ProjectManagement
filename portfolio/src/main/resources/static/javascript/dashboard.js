function updateProjectDetails(projectId, projectName) {
    console.log(projectId);
    console.log(projectName);
    document.getElementById('message').innerText =  `Are you sure you want to delete ${projectName}`;
    document.getElementById('deleteConfirmed').setAttribute('action', `/dashboard/deleteProject/${projectId}`);
}
