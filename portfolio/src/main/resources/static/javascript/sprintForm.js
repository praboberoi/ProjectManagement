/**
 * Script for setting the minimum and maximum values for start and end dates of the selected sprint.
 */

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

