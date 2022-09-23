let currentMilestoneId;
let editingMilestone = false;


/**
 * Function for error validation of Milestone Name field.
 * Display error message if input is invalid.
 */
function checkMilestoneName() {
    const milestoneName = document.getElementById('milestone-name');
    const milestoneCreateBtn = document.getElementById('milestoneFormSubmitButton');
    const milestoneNameError = document.getElementById('milestoneNameError');

    let charMessage = document.getElementById("milestoneCharCount");
    let charCount = milestoneName.value.length;
    charMessage.innerText = charCount + ' '
    if (milestoneName.value.length < 1) {
        milestoneName.classList.add("formError");
        milestoneNameError.innerText = "Milestone Name must not be empty";
        milestoneCreateBtn.disabled = true;
    } else if (milestoneName.value.length < 3) {
        milestoneName.classList.add("formError");
        milestoneNameError.innerText = "Milestone name must be at least 3 characters";
        milestoneCreateBtn.disabled = true;
    } else if (milestoneName.value.length > 50) {
        milestoneName.classList.add("formError");
        milestoneNameError.innerText = "Milestone Name cannot exceed 50 characters";
        milestoneCreateBtn.disabled = true;
    } else {
        milestoneName.classList.remove("formError");
        milestoneNameError.innerText = null;
        milestoneCreateBtn.disabled = false;
    }
}
/**
 * Checks that the date of the milestone is valid
 */
function checkMilestoneDates() {
    const milestoneDateElement = document.getElementById('milestoneDate');
    const milestoneDateError = document.getElementById('milestoneDateError');
    const milestoneCreateBtn = document.getElementById('milestoneFormSubmitButton');

    const checkMilestoneDate = new Date(milestoneDateElement.value);

    milestoneDateElement.setCustomValidity("");
    if (checkMilestoneDate < new Date(projectStartDate + 'T00:00')) {
        milestoneDateError.innerText = "Milestone must start on or after " + new Date(projectStartDate).toLocaleDateString('en-NZ', DATE_OPTIONS);
        milestoneDateElement.classList.add("formError");
        milestoneCreateBtn.disabled = true;
        return;
    } else if (checkMilestoneDate > new Date(projectEndDate + 'T00:00')) {
        milestoneDateError.innerText = "Milestone must end on or before the " + new Date(projectEndDate).toLocaleDateString('en-NZ', DATE_OPTIONS);
        milestoneDateElement.classList.add("formError");
        milestoneCreateBtn.disabled = true;
        return;
    }

    milestoneDateError.innerText = "";
    milestoneDateElement.classList.remove("formError")
    milestoneCreateBtn.disabled = false;
}

/**
 * Notifies the server about the milestone being edited and updates the milestone modal with the selected milestone.
 */
function editMilestone(name, date, id) {
    editingMilestone = true;
    currentMilestoneId = id;
    stompClient.publish({ destination: "/app/milestone/edit", body: JSON.stringify({ 'active': true, 'projectId': projectId, 'milestoneId': currentMilestoneId }) })
    document.getElementById('milestoneFormSubmitLabel').innerText = 'Save';
    document.getElementById('milestoneFromSubmitImg').src = `${apiPrefix}/icons/save-icon.svg`;
    document.getElementById('milestoneFormSubmitButton').disabled = false;
    document.getElementById('milestone-name').classList.remove("formError");
    document.getElementById('milestoneNameError').innerText = null;
    document.getElementById('milestone-name').value = name;
    document.getElementById('milestoneId').value = id;
    document.getElementById('milestoneCharCount').value = name.length;
    document.getElementById('milestoneDate').value = date.replace(" ", "T");
    document.getElementById('milestoneFormTitle').textContent = "Edit milestone";
    document.getElementById('milestoneFormModalError').innerText=""
    const modalElement = document.getElementById('milestoneFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();
}

/**
 *  Notifies the server about the end of the edit and closes the modal
 */
function closeMilestoneModal() {
    if (editingMilestone) {
        stompClient.publish({ destination: "/app/milestone/edit", body: JSON.stringify({ 'active': false, 'projectId': projectId, 'milestoneId': currentMilestoneId }) });
        editingMilestone = false
    }
    const modalElement = document.getElementById('milestoneFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
    modal.hide();
}

/**
 *  Updates the  milestone modal form to create a new milestone
 */
function createMilestone() {
    document.getElementById('milestone-name').classList.remove("formError");
    document.getElementById('milestoneFormSubmitLabel').innerText = 'Create';
    document.getElementById('milestoneFromSubmitImg').src = `${apiPrefix}/icons/create-icon.svg`;
    document.getElementById('milestoneId').value = 0;
    document.getElementById('milestoneNameError').innerText = null;
    document.getElementById('milestoneFormSubmitButton').disabled = false;
    document.getElementById('milestoneDateError').innerText = null;
    document.getElementById('milestone-name').value = "New Milestone";
    document.getElementById('milestoneCharCount').value = "12";
    document.getElementById('milestoneDate').value = new Date().toLocaleDateString("en-CA");
    document.getElementById('milestoneFormTitle').textContent = "Create New Milestone";
    document.getElementById('milestoneFormModalError').innerText=""
    const modalElement = document.getElementById('milestoneFormModal');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement, {
        keyword: false,
        backdrop: "static"
    });
    modal.show();
}

/**
 * Send a request to the server to save the newly created milestone
 */
function saveMilestone() {
    let modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('milestoneFormModal'))
    let modalError = document.getElementById('milestoneFormModalError')
    if (validateForm()) {
        let httpRequest = new XMLHttpRequest();

        httpRequest.onreadystatechange = () => updateModal(httpRequest, modal, modalError)

        httpRequest.open('POST', apiPrefix + `/project/${projectId}/milestone`);

        let formData = new FormData(document.forms.createMilestoneForm)

        httpRequest.send(formData);
    }
}

