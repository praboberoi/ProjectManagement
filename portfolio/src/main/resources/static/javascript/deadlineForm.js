const deadlineDateElement = document.getElementById('deadlineDate');
const deadlineDateError = document.getElementById('deadlineDateError');
const deadlineCreateBtn = document.getElementById('deadlineFormSubmitButton');


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
        deadlineCreateBtn.disabled = true;
    } else if (deadlineName.value.length < 3){
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline name must be at least 3 characters";
        deadlineCreateBtn.disabled = true;
    } else if (deadlineName.value.length > 50){
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline Name cannot exceed 50 characters";
        deadlineCreateBtn.disabled = true;
    } else {
        deadlineName.classList.remove("formError");
        deadlineNameError.innerText = null;
        deadlineCreateBtn.disabled = false;
    }
}
/**
 * Checks that the date of the deadline is valid
 */
function checkDeadlineDates(){
    const checkDeadlineDate = new Date(deadlineDateElement.value);
    deadlineDateElement.setCustomValidity("");
    if (checkDeadlineDate < new Date(projectStartDate + 'T00:00')) {
        deadlineDateError.innerText = "Deadline must start on or after " + new Date(projectStartDate).toLocaleDateString('en-NZ', DATE_OPTIONS);
        deadlineDateElement.classList.add("formError");
        deadlineCreateBtn.disabled = true;
        return;
    } else if (checkDeadlineDate > new Date(projectEndDate + 'T00:00')) {
        deadlineDateError.innerText = "Deadline must end on or before the " + new Date(projectEndDate).toLocaleDateString('en-NZ', DATE_OPTIONS);
        deadlineDateElement.classList.add("formError");
        deadlineCreateBtn.disabled = true;
        return;
    }
    deadlineDateError.innerText = "";
    deadlineDateElement.classList.remove("formError")
    deadlineCreateBtn.disabled = false;
}
