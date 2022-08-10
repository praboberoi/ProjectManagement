const deadlineDateElement = document.getElementById('deadlineDate');
const deadlineDateError = document.getElementById('deadlineDateError');

/**
 * Function for error validation of Deadline Name field.
 * Display error message if input is invalid.
 */
function checkDeadlineName() {
    const deadlineName = document.getElementById('deadline-name');
    const deadlineNameError = document.getElementById('deadlineNameError');
    let charMessage = document.getElementById("deadlineCharCount");
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

    if (checkDeadlineDate < new Date(projectStartDate)) {
        deadlineDateError.innerText = "Deadline must start after " + new Date(projectStartDate).toLocaleDateString('en-NZ', DATE_OPTIONS);
        deadlineDateElement.classList.add("formError");
        return;
    } else if (checkDeadlineDate > new Date(projectEndDate)) {
        deadlineDateError.innerText = "Deadline must end before " + new Date(projectEndDate).toLocaleDateString('en-NZ', DATE_OPTIONS);
        deadlineDateElement.classList.add("formError");
        return;
    }
    deadlineDateError.innerText = "";
    deadlineDateElement.classList.remove("formError")
}
