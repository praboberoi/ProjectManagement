/**
 * Script for setting the minimum and maximum values for start and end dates of the selected project.
 */

const startDateElement = document.querySelector('#startDate');
const endDateElement = document.querySelector('#endDate');
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

//initialDateSetup();