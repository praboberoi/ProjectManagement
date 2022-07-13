const projectDescriptionNav = document.getElementById('projectDescriptionNav')
const plannerNav = document.getElementById('plannerNav')
const calender = document.getElementById('cal')
const projectDescription = document.getElementById('projectDescription')

function updateSprintDetails(sprintId, sprintName, projectId, prefix) {
    if (prefix === null)
        prefix = ""
    document.getElementById('message').innerText = `Are you sure you want to delete ${sprintName}`;
    document.getElementById('deleteSprint').setAttribute('action', `${prefix}/${projectId}/deleteSprint/${sprintId}`);
}

function navTOProjectDescription() {
    if (projectDescriptionNav.ariaSelected === "false") {
        calender.hidden = true
        projectDescription.hidden = false
        projectDescriptionNav.ariaSelected = "true";
        plannerNav.ariaSelected = "false"
        plannerNav.classList.remove('active')
        projectDescriptionNav.classList.add('active')

    }
}

function navToPlanner() {
    if (plannerNav.ariaSelected === "false") {
        calender.hidden = false
        projectDescription.hidden = true
        projectDescriptionNav.ariaSelected = "false";
        plannerNav.ariaSelected = "true"
        projectDescriptionNav.classList.remove('active')
        plannerNav.classList.add('active')
    }

}