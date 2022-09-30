const deadlineDateElement = document.getElementById('deadlineDate');
const deadlineDateError = document.getElementById('deadlineDateError');
const deadlineCreateBtn = document.getElementById('deadlineFormSubmitButton');
let currentDeadlineId;
let editingDeadline = false;



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
        deadlineNameError.innerText = "Deadline name must not be empty";
        deadlineCreateBtn.disabled = true;
    } else if (deadlineName.value.length < 3){
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline name must be at least 3 characters";
        deadlineCreateBtn.disabled = true;
    } else if (deadlineName.value.length > 50){
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline name cannot exceed 50 characters";
        deadlineCreateBtn.disabled = true;
    } else if (emojiRegx.test(deadlineName.value)) {
        deadlineName.classList.add("formError");
        deadlineNameError.innerText = "Deadline name must not contain an emoji";
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
 * Notifies the server about the deadline being edited and updates the deadline modal with the selected deadline.
 */
function editDeadline(name, date, id) {
    editingDeadline = true;
    currentDeadlineId = id;
    stompClient.publish({destination: "/app/deadline/edit", body: JSON.stringify({'active': true, 'projectId': projectId, 'deadlineId': currentDeadlineId})})
    document.getElementById('deadlineFormSubmitLabel').innerText = 'Save';
    document.getElementById('deadlineFromSubmitImg').src = `${apiPrefix}/icons/save-icon.svg`;
    document.getElementById('deadlineFormSubmitButton').disabled = false;
    document.getElementById('deadline-name').classList.remove("formError");
    document.getElementById('deadlineNameError').innerText = null;
    document.getElementById('deadline-name').value = name;
    document.getElementById('deadlineId').value = id;
    document.getElementById('deadlineCharCount').value = name.length;
    document.getElementById('deadlineDate').value = date.replace(" ", "T");
    document.getElementById('deadlineFormTitle').textContent = "Edit deadline";
    const modalElement = document.getElementById('deadlineFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();
}

/**
 *  Notifies the server about the end of the edit and closes the modal
 */
function closeDeadlineModal() {
    if (editingDeadline) {
        stompClient.publish({destination: "/app/deadline/edit", body: JSON.stringify({'active': false, 'projectId': projectId, 'deadlineId': currentDeadlineId})});
        editingDeadline = false
    }
    const modalElement = document.getElementById('deadlineFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
    modal.hide();
}

/**
 *  Updates the  deadline modal form to create a new deadline
 */
function createDeadline() {
    document.getElementById('deadline-name').classList.remove("formError");
    document.getElementById('deadlineFormSubmitLabel').innerText = 'Create';
    document.getElementById('deadlineFromSubmitImg').src = `${apiPrefix}/icons/create-icon.svg`;
    document.getElementById('deadlineId').value = 0;
    document.getElementById('deadlineNameError').innerText = null;
    document.getElementById('deadlineFormSubmitButton').disabled = false;
    document.getElementById('deadlineDateError').innerText = null;
    document.getElementById('deadline-name').value = "New Deadline";
    document.getElementById('deadlineCharCount').value = "12";
    document.getElementById('deadlineDate').value = new Date().toLocaleDateString("en-CA") + 'T00:00';
    document.getElementById('deadlineFormTitle').textContent = "Create New Deadline";
    const modalElement = document.getElementById('deadlineFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();
}

/**
 * Send a request to the server to save the newly created deadline
 */
function saveDeadline() {
    if ( validateForm() ) {
        let httpRequest = new XMLHttpRequest();

        httpRequest.onreadystatechange = () => processAction(httpRequest)

        httpRequest.open('POST', apiPrefix + `/project/${projectId}/saveDeadline`);

        let formData = new FormData(document.forms.createDeadlineForm)

        httpRequest.send(formData);

    }
}

