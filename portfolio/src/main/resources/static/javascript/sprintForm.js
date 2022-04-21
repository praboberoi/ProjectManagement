/**
 * Script for setting the minimum and maximum values for start and end dates of the selected sprint.
 */


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

startDateElement.addEventListener('change', (event) => {
    const startDate = document.getElementById("startDate").value
    const endDate = document.querySelector('#endDate');
    endDate.setAttribute("min", startDate);
});

const endDateElement = document.querySelector('#endDate');


/**
 * An Event listener for triggering the required change for setting minimum and maximum values of dates.
 */
endDateElement.addEventListener('change',(event) => {
    const endDate = document.getElementById("endDate").value
    const startDate = document.querySelector('#startDate')
    startDate.setAttribute("max", endDate)
});
initialDateSetup();
