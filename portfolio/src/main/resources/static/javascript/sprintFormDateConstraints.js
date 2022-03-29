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

endDateElement.addEventListener('change',(event) => {
    const endDate = document.getElementById("endDate").value
    const startDate = document.querySelector('#startDate')
    startDate.setAttribute("max", endDate)
});
initialDateSetup();
