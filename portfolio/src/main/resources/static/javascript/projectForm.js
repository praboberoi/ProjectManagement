/**
 * Script for setting the minimum and maximum values for start and end dates of the selected project.
 * Check and display errors in project name, min and max dates.
 */

const projectNameRegex = /^[A-Za-z0-9]+(?: +[A-Za-z0-9]+)*$/

const startDateElement = document.querySelector('#startDate');
const endDateElement = document.querySelector('#endDate');

function check_projectName() {
    let projectName = document.getElementById('project-name');
    let projectNameError = document.getElementById('projectNameError');
    if (projectName.value.length < 1) {
        projectName.classList.add("form_error");
        projectNameError.innerText = "Project Name must not be empty";
    } else if (! projectNameRegex.test(projectName.value)) {
        projectName.classList.add("form_error");
        projectNameError.innerText = "Project Name must not start or end with space characters";
    } else {
        projectName.classList.remove("form_error");
        projectNameError.innerText = null;
    }
}

/**
 * Function that initialises the minimum and maximum date values when the project form is first opened.
 */
function initialDateSetup() {

    const startDateMin = new Date(document.getElementById('startDate').value);
    startDateMin.setFullYear(startDateMin.getFullYear() - 1);

    const startDateMax = new Date(document.getElementById('endDate').value);
    startDateMax.setDate(startDateMax.getDate() - 1);

    const endDateMin = new Date(document.getElementById('startDate').value);
    endDateMin.setDate(endDateMin.getDate() + 1);

    const endDateMax = new Date(document.getElementById('startDate').value);
    endDateMax.setFullYear(endDateMax.getFullYear() + 10);
    startDateElement.setAttribute("min",`${startDateMin.toISOString().slice(0, 10)}`);
    startDateElement.setAttribute('max', `${startDateMax.toISOString().slice(0, 10)}`);

    endDateElement.setAttribute("min", `${endDateMin.toISOString().slice(0, 10)}`);
    endDateElement.setAttribute("max", `${endDateMax.toISOString().slice(0, 10)}`);

}

/**
 * An Event listener for triggering the required change for setting minimum and maximum values of dates.
 */
startDateElement.addEventListener('change', (event) => {

    const endDateMin = new Date(document.getElementById("startDate").value);
    endDateMin.setDate(endDateMin.getDate() + 1);

    const endDateMax = new Date(document.getElementById("startDate").value);
    endDateMax.setFullYear(endDateMax.getFullYear() + 10);

    endDateElement.setAttribute("min", `${endDateMin.toISOString().slice(0, 10)}`);
    endDateElement.setAttribute("max", `${endDateMax.toISOString().slice(0, 10)}`);
});

endDateElement.addEventListener('change',(event) => {

    const startDateMin = new Date(document.getElementById("startDate").value);
    startDateMin.setFullYear(startDateMin.getFullYear() - 1);

    const startDateMax = new Date(document.getElementById("endDate").value);
    startDateMax.setDate(startDateMax.getDate() - 1);

    startDateElement.setAttribute("min", `${startDateMin.toISOString().slice(0, 10)}`);
    startDateElement.setAttribute("max", `${startDateMax.toISOString().slice(0, 10)}`);
});

initialDateSetup();