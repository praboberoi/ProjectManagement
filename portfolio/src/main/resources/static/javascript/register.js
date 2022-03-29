var special = /[ `!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/;
var names = /[`!@#$%^&*()_+\=\[\]{};:"\\|,.<>\/?~]/;
var digit = /[0-9]/;
var upper = /[A-Z]/;

function check_username() {
    let usernameElement = document.getElementById("username");
    let usernameErrorElement = document.getElementById("usernameError");
    if (usernameElement.value.length < 3 
        || usernameElement.value.length > 16
    ) {
        usernameElement.classList.add("form_error")
        usernameErrorElement.innerText = "Username must be between 3 and 16 characters."
    } else {
        usernameElement.classList.remove("form_error");
        usernameErrorElement.innerText = null;
    }
}

function check_firstName() {
    let firstNameElement = document.getElementById("firstName");
    let firstNameErrorElement = document.getElementById("firstNameError");
    if (firstNameElement.value.length < 3 
        || firstNameElement.value.length > 32 
        || names.test(firstNameElement.value) 
        || digit.test(firstNameElement.value))
        {
        firstNameElement.classList.add("form_error")
        firstNameErrorElement.innerText = "First name must be between 3 and 32 characters with no special characters or digits."
    } else {
        firstNameElement.classList.remove("form_error");
        firstNameErrorElement.innerText = null;
    }
}

function check_lastName() {
    let lastNameElement = document.getElementById("lastName");
    let lastNameErrorElement = document.getElementById("lastNameError");
    if (lastNameElement.value.length < 3 
        || lastNameElement.value.length > 32 
        || names.test(lastNameElement.value) 
        || digit.test(lastNameElement.value))
        {
        lastNameElement.classList.add("form_error")
        lastNameErrorElement.innerText = "Last name must be between 3 and 32 characters with no special characters or digits."
    } else {
        lastNameElement.classList.remove("form_error");
        lastNameErrorElement.innerText = null;
    }
}

function check_nickname() {
    let nicknameElement = document.getElementById("nickname");
    let nicknameErrorElement = document.getElementById("nicknameError");
    if (nicknameElement.value.length > 32)
        {
        nicknameElement.classList.add("form_error")
        nicknameErrorElement.innerText = "Nickname must be less than 32 characters."
    } else {
        nicknameElement.classList.remove("form_error");
        nicknameErrorElement.innerText = null;
    }
}

function check_email() {
    let nicknameElement = document.getElementById("nickname");
    let nicknameErrorElement = document.getElementById("nicknameError");
    nicknameElement.classList.remove("form_error");
    nicknameErrorElement.innerText = null;
}

function check_pronouns() {
    let pronounsElement = document.getElementById("pronouns");
    let pronounsErrorElement = document.getElementById("personalPronounsError");
    if (pronounsElement.value.length > 32)
        {
        pronounsElement.classList.add("form_error")
        pronounsErrorElement.innerText = "Personal pronouns must be less than 32 characters."
    } else {
        pronounsElement.classList.remove("form_error");
        pronounsErrorElement.innerText = null;
    }
}

function check_pass() {
    let passwordElement = document.getElementById("password");
    let passwordErrorElement = document.getElementById("passwordError");

    if (passwordElement.value.length < 8 
        || passwordElement.value.length > 63
        || !upper.test(passwordElement.value)
        || !digit.test(passwordElement.value)){
        passwordElement.classList.add("form_error")
        passwordErrorElement.innerText = "Password must contain 8-16 characters, a digit and uppercase character."
    } else {
        passwordElement.classList.remove("form_error")
        passwordErrorElement.innerText = null
    }
}

function match_pass() {
    let passwordElement = document.getElementById("password");
    let confirmPasswordElement = document.getElementById("confirmPassword");
    let confirmPasswordErrorElement = document.getElementById("confirmPasswordError");
    if (passwordElement.value ==
        confirmPasswordElement.value) {
        if (confirmPasswordElement.classList.contains("form_error")) {
            confirmPasswordElement.classList.remove("form_error");
        }
        confirmPasswordElement.classList.remove("form_error")
        confirmPasswordErrorElement.innerText = null
    } else {
        confirmPasswordElement.classList.add("form_error")
        confirmPasswordErrorElement.innerText = "Passwords do not match."
    }
}

function check_bio() {
    let bioElement = document.getElementById("bio");
    let bioErrorElement = document.getElementById("bioError");
    if (bioElement.value.length > 250)
    {
        bioErrorElement.classList.add("form_error");
        bioErrorElement.innerText = "Bio must be less than 250 characters."
    } else {
        bioErrorElement.classList.remove("form_error");
        bioErrorElement.innerText = null;
    }
};