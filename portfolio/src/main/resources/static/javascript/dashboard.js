const deleteButton = document.querySelector('#delProject');

deleteButton.addEventListener('click', () => {
    const data =  deleteButton.title.split( ' ');
    const projectId = data[0];
    const projectName = data.slice(1).join(' ');
    const modalForm = document.querySelector('#deleteProjectModalForm');
    modalForm.action = `/dashboard/deleteProject/${projectId}`;

    const modalBody = document.querySelector('#modalBody');
    const para = document.createElement("p");
    const node = document.createTextNode(`Are you sure you want to delete ${projectName}`);
    para.appendChild(node);
    modalBody.appendChild(para);
})
