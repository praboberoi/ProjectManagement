var editBtn = document.getElementById('editBtn');
var editables = document.querySelectorAll('#username, #firstname, #lastname');
var pronoun = /[`!@#$%^*()_+\-&,\=\[\]{};':"\\|.<>\?~]/;
var email = /^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/;
var names = /[`!@#$%^&*()_+\=\[\]{};:"\\|,.<>\/?~]/;
var digit = /[0-9]/;
var upper = /[A-Z]/;
var dash = /^[A-Za-z0-9 ]+(-[A-Za-z0-9 ]+)*$/;
var space = /^[A-Za-z0-9\-]+( [A-Za-z0-9\-]+)*$/;

/**
 * Validates the users first name in input field
 */
function checkFirstName() {
    let firstNameElement = document.getElementById("firstName");
    let firstNameErrorElement = document.getElementById("firstNameError");
    if (firstNameElement.value.length < 2
        || firstNameElement.value.length > 32
        || names.test(firstNameElement.value)
        || digit.test(firstNameElement.value)
        || !dash.test(firstNameElement.value)
        || !space.test(firstNameElement.value)) {
        firstNameElement.classList.add("formError")
        firstNameErrorElement.innerText = "First name must be between 2 and 32 characters with no special characters or digits."
        firstNameElement.setCustomValidity("Invalid field.");
    } else {
        firstNameElement.classList.remove("formError");
        firstNameErrorElement.innerText = null;
        firstNameElement.setCustomValidity("");
    }
};

/**
 * Validates the users last name in input field
 */
function checkLastName() {
    let lastNameElement = document.getElementById("lastName");
    let lastNameErrorElement = document.getElementById("lastNameError");
    if (lastNameElement.value.length < 2
        || lastNameElement.value.length > 32
        || names.test(lastNameElement.value)
        || digit.test(lastNameElement.value)
        || !dash.test(lastNameElement.value)
        || !space.test(lastNameElement.value)) {
        lastNameElement.classList.add("formError")
        lastNameErrorElement.innerText = "Last name must be between 2 and 32 characters with no special characters or digits."
        lastNameElement.setCustomValidity("Invalid field.");
    } else {
        lastNameElement.classList.remove("formError");
        lastNameErrorElement.innerText = null;
        lastNameElement.setCustomValidity("");
    }
};

/**
 * Validates the users nickname in input field
 */
function checkNickname() {
    let nicknameElement = document.getElementById("nickname");
    let nicknameErrorElement = document.getElementById("nicknameError");
    if (nicknameElement.value.length > 32)
    {
        nicknameElement.classList.add("formError")
        nicknameErrorElement.innerText = "Nickname must be less than 32 characters."
        nicknameElement.setCustomValidity("Invalid field.");
    } else {
        nicknameElement.classList.remove("formError");
        nicknameErrorElement.innerText = null;
        nicknameElement.setCustomValidity("");
    }
};

/**
 * Validates the users email in input field
 */
function checkEmail() {
    let emailElement = document.getElementById("email");
    let emailErrorElement = document.getElementById("emailError");
    if (!email.test(emailElement.value))
    {
        emailElement.classList.add("formError")
        emailErrorElement.innerText = "Email must be in the form username@domainName.domain."
        emailElement.setCustomValidity("Invalid field.");
    } else {
        emailElement.classList.remove("formError");
        emailErrorElement.innerText = null;
        emailElement.setCustomValidity("");
    }
};

/**
 * Validates the users pronouns in input field
 */
function checkPronouns() {
    let pronounsElement = document.getElementById("pronouns");
    let pronounsErrorElement = document.getElementById("personalPronounsError");
    if (pronounsElement.value.length > 32) {
        pronounsElement.classList.add("formError")
        pronounsErrorElement.innerText = "Personal pronouns must be less than 32 characters."
        pronounsElement.setCustomValidity("Invalid field.");
    } else if (digit.test(pronounsElement.value)
        || pronoun.test(pronounsElement.value)) {
        pronounsElement.classList.add("formError")
        pronounsErrorElement.innerText = "Personal pronouns can only contain special character / and no digits."
        pronounsElement.setCustomValidity("Invalid field.");
    } else {
        pronounsElement.classList.remove("formError");
        pronounsErrorElement.innerText = null;
        pronounsElement.setCustomValidity("");
    }
};

/**
 * Validates the users bio in input field
 */
function checkBio() {
    let bioElement = document.getElementById("bio");
    let bioErrorElement = document.getElementById("bioError");
    let charMessage = document.getElementById("charCount");
    let charCount = bioElement.value.length;
    charMessage.innerText = charCount + ' '

    if (bioElement.value.length > 250)
    {
        bioErrorElement.classList.add("formError");
        bioErrorElement.innerText = "Bio must be less than 250 characters."
        bioElement.setCustomValidity("Invalid field.");
    } else {
        bioErrorElement.classList.remove("formError");
        bioErrorElement.innerText = null;
        bioElement.setCustomValidity("");
    }
};
