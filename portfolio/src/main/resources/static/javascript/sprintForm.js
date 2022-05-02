/**
 * Script for setting the minimum and maximum values for start and end dates of the selected sprint.
 */

// Regular expression for Sprint Name field. No leading white spaces or empty field.
const sprintNameRegex = /^[A-Za-z0-9]+(?: +[A-Za-z0-9]+)*$/

/**
 * Function for error validation of Sprint Name field.
 * Display error message if input is invalid.
  */
function checkSprintName() {
    let sprintName = document.getElementById('sprint-name');
    let sprintNameError = document.getElementById('sprintNameError');
    if (sprintName.value.length < 1) {
        sprintName.classList.add("form_error");
        sprintNameError.innerText = "Sprint Name must not be empty";
    } else if (! sprintNameRegex.test(sprintName.value)) {
        sprintName.classList.add("form_error");
        sprintNameError.innerText = "Sprint Name must not start or end with space characters";
    } else {
        sprintName.classList.remove("form_error");
        sprintNameError.innerText = null;
    }
}

/**
 * Function that initialises the minimum and maximum date values when the sprint form is first opened.
 */
function initialDateSetup() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    const currentProject = document.getElementById('project-end-date').value;

    const startDateInput = document.querySelector('#startDate')
    const endDateInput = document.querySelector('#endDate')

    startDateInput.setAttribute('min', startDate);
    startDateInput.setAttribute('max', endDate);
    endDateInput.setAttribute('min', startDate);
    endDateInput.setAttribute('max', currentProject);
}
const startDateElement = document.querySelector('#startDate');

/**
 * An Event listener for triggering the required change for setting minimum values of dates.
 */
startDateElement.addEventListener('change', (event) => {
    const startDate = document.getElementById("startDate").value
    const endDate = document.querySelector('#endDate');
    endDate.setAttribute("min", startDate);
});

const endDateElement = document.querySelector('#endDate');


/**
 * An Event listener for triggering the required change for setting maximum values of dates.
 */
endDateElement.addEventListener('change',(event) => {
    const endDate = document.getElementById("endDate").value
    const startDate = document.querySelector('#startDate')
    startDate.setAttribute("max", endDate)
});

