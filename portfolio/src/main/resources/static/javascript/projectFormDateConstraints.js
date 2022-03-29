/**
 * Script for setting the minimum and maximum values for start and end dates of the selected project.
 */


/**
 * Function that initialises the minimum and maximum date values when the project form is first opened.
 */
function initialDateSetup() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    const startDateInput = document.querySelector('#startDate')
    const endDateInput = document.querySelector('#endDate')

    const startDateMin = new Date();
    startDateMin.setFullYear(startDateMin.getFullYear() - 1);

    const startDateMinStr = startDateMin.getFullYear() + '-' + formatDate(startDateMin.getMonth() + 1) +
        '-' + formatDate(startDateMin.getDate());

    const endDateMin = new Date(Date.parse(startDate));
    endDateMin.setDate(endDateMin.getDate() + 1);

    const startDateMax = new Date(Date.parse(endDate));
    startDateMax.setDate(startDateMax.getDate() - 1);
    const startDateMaxStr = startDateMax.getFullYear() + '-' + formatDate(startDateMax.getMonth() + 1) +
        '-' + formatDate(startDateMax.getDate());

    const endDateMinStr = endDateMin.getFullYear() + '-' + formatDate(endDateMin.getMonth() + 1) +
        '-' + formatDate(endDateMin.getDate());

    startDateInput.setAttribute('max', startDateMaxStr);
    startDateInput.setAttribute('min', (startDateMinStr));
    endDateInput.setAttribute('min', endDateMinStr);
}
const startDateElement = document.querySelector('#startDate');

/**
 * Function for date formatting to obtain a double character string if the given number is less than 10
 * @param date number value
 * @returns {string} a double-digit value
 */
function formatDate(date) {
    let newDate = (date);
    if (newDate<10){
        newDate= '0' +newDate;
    }

    return newDate;
}

/**
 * An Event listener for triggering the required change for setting minimum and maximum values of dates.
 */
startDateElement.addEventListener('change', (event) => {
    const startDate = document.getElementById("startDate").value
    const endDate = document.querySelector('#endDate');

    const endDateMin = new Date(Date.parse(startDate));
    endDateMin.setDate(endDateMin.getDate() + 1);

    const endDateMinStr = endDateMin.getFullYear() + '-' + formatDate(endDateMin.getMonth() + 1) +
        '-' + formatDate(endDateMin.getDate());


    endDate.setAttribute("min", endDateMinStr);
});

const endDateElement = document.querySelector('#endDate');

endDateElement.addEventListener('change',(event) => {
    const endDate = document.getElementById("endDate").value
    const startDate = document.querySelector('#startDate')

    const startDateMax = new Date(Date.parse(endDate));
    startDateMax.setDate(startDateMax.getDate() - 1);
    const startDateMaxStr = startDateMax.getFullYear() + '-' + formatDate(startDateMax.getMonth() + 1) +
        '-' + formatDate(startDateMax.getDate());

    startDate.setAttribute("max", startDateMaxStr)
});
initialDateSetup();
