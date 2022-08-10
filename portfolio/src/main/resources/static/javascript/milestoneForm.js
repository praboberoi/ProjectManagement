const projectId = document.getElementById("projectId").value;
const dateElement = document.getElementById('date');
const dateError = document.getElementById('dateError');
const projectStartDate = new Date(document.getElementById("projectStartDate").value);
const projectEndDate = new Date(document.getElementById("projectEndDate").value);
const DATE_OPTIONS = { year: 'numeric', month: 'short', day: 'numeric' };

/**
 * Function for error validation of Milestone Name field.
 * Display error message if input is invalid.
 */
function checkMilestoneName() {
    const milestoneName = document.getElementById('name');
    const milestoneNameError = document.getElementById('milestoneNameError');
    let charMessage = document.getElementById("charCount");
    let charCount = milestoneName.value.length;
    charMessage.innerText = charCount + ' '

    if (milestoneName.value.length < 1) {
        milestoneName.classList.add("formError");
        milestoneNameError.innerText = "Milestone Name must not be empty";
    } else if (milestoneName.value.length > 50){
        milestoneName.classList.add("formError");
        milestoneNameError.innerText = "Milestone Name cannot exceed 50 characters";
    } else {
        milestoneName.classList.remove("formError");
        milestoneNameError.innerText = null;
    }
}


/**
 * Checks that the Milestone Date is valid
 */
function checkMilestoneDate() {
    dateElement.setCustomValidity("");

    checkDate();


    if(dateElement.classList.contains("formError")) {
        document.getElementById("formSubmitButton").disabled = true;
        return;
    } else {
        document.getElementById("formSubmitButton").disabled = false;
    }
}

/**
 * Checks that the date of the milestone is within the project
 */
function checkDate() {
    const date = new Date(dateElement.value);

    if (date < projectStartDate) {
        dateError.innerText = "Milestone must be after " + projectStartDate.toLocaleDateString('en-NZ', DATE_OPTIONS);
        dateElement.classList.add("formError");
        return;
    } else if (date > projectEndDate) {
        dateError.innerText = "Milestone must be before the project ends";
        dateElement.classList.add("formError")
        return;
    }

    dateError.innerText = "";
    dateElement.classList.remove("formError")
}




