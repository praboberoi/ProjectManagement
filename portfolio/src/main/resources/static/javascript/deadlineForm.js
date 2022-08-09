const deadlineDateElement = document.getElementById('deadlineDate');
const deadlineDateError = document.getElementById('deadlineDateError');
const projectStartDate = new Date(document.getElementById("projectStartDate").value);
const projectEndDate = new Date(document.getElementById("projectEndDate").value);
const DATE_OPTIONS = { year: 'numeric', month: 'short', day: 'numeric' };


/**
 * Function for error validation of Deadline Name field.
 * Display error message if input is invalid.
 */
function checkDeadlineName() {
    const deadlineName = document.getElementById('deadline-name');
    const deadlineNameError = document.getElementById('deadlineNameError');
    let charMessage = document.getElementById("charCount");
    let charCount = deadlineName.value.length;
    charMessage.innerText = charCount + ' '

    if (deadlineName.value.length < 1) {
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline Name must not be empty";
    } else if (deadlineName.value.length < 3){
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline name must be at least 3 characters";
    } else if (deadlineName.value.length > 50){
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline Name cannot exceed 50 characters";
    } else {
        deadlineName.classList.remove("formError");
        deadlineNameError.innerText = null;
    }
}

/**
 * Checks that the date of the deadline is valid
 */
function checkDeadlineDates(){
    const checkDeadlineDate = new Date(deadlineDateElement.value);
    deadlineDateElement.setCustomValidity("");

    if (checkDeadlineDate < projectStartDate) {
        deadlineDateError.innerText = "Deadline must start after " + projectStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        deadlineDateElement.classList.add("formError");
        return;
    } else if (checkDeadlineDate > projectEndDate) {
        deadlineDateError.innerText = "Deadline must end before " + projectEndDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        deadlineDateElement.classList.add("formError");
        return;
    }
    deadlineDateError.innerText = "";
    deadlineDateElement.classList.remove("formError")
}
