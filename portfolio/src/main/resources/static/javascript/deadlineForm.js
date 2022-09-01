const deadlineDateElement = document.getElementById('deadlineDate');
const deadlineDateError = document.getElementById('deadlineDateError');
const deadlineCreateBtn = document.getElementById('deadlineFormSubmitButton');
let currentDeadlineId;


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

/**
 * Updates the deadline modal form to edit the chosen deadline and shows the modal
 */
function editDeadline(name, date, id) {
    currentDeadlineId = id;
    document.getElementById('deadlineFormSubmitButton').disabled = false;
    document.getElementById('deadline-name').classList.remove("formError");
    document.getElementById('deadlineNameError').innerText = null;
    document.getElementById('deadlineDateError');
    document.getElementById('deadline-name').value = name;
    document.getElementById('deadlineId').value = id;
    document.getElementById('deadlineCharCount').value = name.length;
    document.getElementById('deadlineDate').value = date;
    document.getElementById('deadlineFormTitle').textContent = "Edit deadline";
    const modalElement = document.getElementById('deadlineFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();
}

/**
 * Closes the modal
 */
function closeDeadlineModal() {
    currentDeadlineId = null;
    const modalElement = document.getElementById('deadlineFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
    modal.hide();
}

/**
 *  Updates the deadline modal form to create a new deadline and shows the modal
 */
function createDeadline() {
    document.getElementById('deadline-name').classList.remove("formError");
    document.getElementById('deadlineNameError').innerText = null;
    document.getElementById('deadlineFormSubmitButton').disabled = false;
    document.getElementById('deadline-name').value = "New Deadline";
    document.getElementById('deadlineCharCount').value = "12";
    document.getElementById('deadlineDate').value = new Date().toLocaleDateString().split('/').reverse().join('-') + 'T00:00';
    document.getElementById('deadlineFormTitle').textContent = "Create New Deadline";
    const modalElement = document.getElementById('deadlineFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();
}

/**
 * Sends a notification to the server when a user starts editing an event
 */
document.getElementById('deadlineFormModal').addEventListener('shown.bs.modal', function () {
    stompClient.publish({destination: "/app/event/edit", body: JSON.stringify({'active': true, 'projectId': projectId, 'deadlineId': currentDeadlineId})})
});

/**
 * Sends a notification to the server when a user stops editing an event
 */
document.getElementById('deadlineFormModal').addEventListener('hidden.bs.modal', function () {
    stompClient.publish({destination: "/app/event/edit", body: JSON.stringify({'active': false, 'projectId': projectId, 'deadlineId': currentDeadlineId})})
});
