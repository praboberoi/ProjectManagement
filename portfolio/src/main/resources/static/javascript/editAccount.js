var editBtn = document.getElementById('editBtn');
var editables = document.querySelectorAll('#username, #firstname, #lastname');
var special = /[`!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/;
var email = /^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/;
var names = /[`!@#$%^&*()_+\=\[\]{};:"\\|,.<>\/?~]/;
var digit = /[0-9]/;
var upper = /[A-Z]/;

function checkFirstName() {
    let firstNameElement = document.getElementById("firstName");
    let firstNameErrorElement = document.getElementById("firstNameError");
    if (firstNameElement.value.length < 3
        || firstNameElement.value.length > 32
        || names.test(firstNameElement.value)
        || digit.test(firstNameElement.value))
    {
        firstNameElement.classList.add("formError")
        firstNameErrorElement.innerText = "First name must be between 3 and 32 characters with no special characters or digits."
    } else {
        firstNameElement.classList.remove("formError");
        firstNameErrorElement.innerText = null;
    }
};

function checkLastName() {
    let lastNameElement = document.getElementById("lastName");
    let lastNameErrorElement = document.getElementById("lastNameError");
    if (lastNameElement.value.length < 3
        || lastNameElement.value.length > 32
        || names.test(lastNameElement.value)
        || digit.test(lastNameElement.value))
    {
        lastNameElement.classList.add("formError")
        lastNameErrorElement.innerText = "Last name must be between 3 and 32 characters with no special characters or digits."
    } else {
        lastNameElement.classList.remove("formError");
        lastNameErrorElement.innerText = null;
    }
};

function checkNickname() {
    let nicknameElement = document.getElementById("nickname");
    let nicknameErrorElement = document.getElementById("nicknameError");
    if (nicknameElement.value.length > 32)
    {
        nicknameElement.classList.add("formError")
        nicknameErrorElement.innerText = "Nickname must be less than 32 characters."
    } else {
        nicknameElement.classList.remove("formError");
        nicknameErrorElement.innerText = null;
    }
};

function checkEmail() {
    let emailElement = document.getElementById("email");
    let emailErrorElement = document.getElementById("emailError");
    if (!email.test(emailElement.value))
    {
        emailElement.classList.add("formError")
        emailErrorElement.innerText = "Email must be in the form username@domainName.domain."
    } else {
        emailElement.classList.remove("formError");
        emailErrorElement.innerText = null;
    }
};

function checkPronouns() {
    let pronounsElement = document.getElementById("pronouns");
    let pronounsErrorElement = document.getElementById("personalPronounsError");
    if (pronounsElement.value.length > 32)
    {
        pronounsElement.classList.add("formError")
        pronounsErrorElement.innerText = "Personal pronouns must be less than 32 characters."
    } else {
        pronounsElement.classList.remove("formError");
        pronounsErrorElement.innerText = null;
    }
};

function checkBio() {
    let bioElement = document.getElementById("bio");
    let bioErrorElement = document.getElementById("bioError");
    if (bioElement.value.length > 250)
    {
        bioErrorElement.classList.add("formError");
        bioErrorElement.innerText = "Bio must be less than 250 characters."
    } else {
        bioErrorElement.classList.remove("formError");
        bioErrorElement.innerText = null;
    }
};
