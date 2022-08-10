var email = /^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-]([A-Za-z0-9-])*(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/;
var names = /[`!@#$%^&*()_+\=\[\]{};:"\\|,.<>\/?~]/;
var username = /[ `!@#$%^&*()_+\=\[\]{};:"\\|,.<>\/?~'-]/;
var digit = /[0-9]/;
var upper = /[A-Z]/;
var special = /[`!@#$%^*()_\=\[\]{};':"\\|.<>\/?~]/;
var dash = /^[A-Za-z0-9 ]+(-[A-Za-z0-9 ]+)*$/;
var space = /^[A-Za-z0-9\-]+( [A-Za-z0-9\-]+)*$/;


/**
 * Validates the user's username in input field
 */
function checkUsername() {
    let usernameElement = document.getElementById("username");
    let usernameErrorElement = document.getElementById("usernameError");
    if (usernameElement.value.length < 3 
        || usernameElement.value.length > 16
        || username.test(usernameElement.value))
    {
        usernameElement.classList.add("formError")
        usernameErrorElement.innerText = "Username must be between 3 and 16 characters with no special characters."
        usernameElement.setCustomValidity("Invalid field.");
    } else {
        usernameElement.classList.remove("formError");
        usernameErrorElement.innerText = null;
        usernameElement.setCustomValidity("");
    }
}

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
        || !space.test(firstNameElement.value))
    {
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
        || !space.test(lastNameElement.value))
    {
        lastNameElement.classList.add("formError")
        lastNameErrorElement.innerText = "Last name must be between 2 and 32 characters with no special characters or digits."
        lastNameElement.setCustomValidity("Invalid field.");
    } else {
        lastNameElement.classList.remove("formError");
        lastNameErrorElement.innerText = null;
        lastNameElement.setCustomValidity("");
    }
}

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
}

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
}

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
        || special.test(pronounsElement.value)) {
        pronounsElement.classList.add("formError")
        pronounsErrorElement.innerText = "Personal pronouns can only contain special characters + & - , and no digits."
        pronounsElement.setCustomValidity("Invalid field.");
    } else {
        pronounsElement.classList.remove("formError");
        pronounsErrorElement.innerText = null;
        pronounsElement.setCustomValidity("");
    }
}

/**
 * Validates the users password in input field
 */
function checkPass() {
    let passwordElement = document.getElementById("password");
    let passwordErrorElement = document.getElementById("passwordError");

    if (passwordElement.value.length < 8 
        || passwordElement.value.length > 63
        || !upper.test(passwordElement.value)
        || !digit.test(passwordElement.value)){
        passwordElement.classList.add("formError")
        passwordErrorElement.innerText = "Password must contain 8-16 characters, a digit and uppercase character."
    } else {
        passwordElement.classList.remove("formError")
        passwordErrorElement.innerText = null
    }
}

/**
 * Validates the users passwords match in input field
 */
function match_pass() {
    let passwordElement = document.getElementById("password");
    let confirmPasswordElement = document.getElementById("confirmPassword");
    let confirmPasswordErrorElement = document.getElementById("confirmPasswordError");
    if (passwordElement.value ==
        confirmPasswordElement.value) {
        if (confirmPasswordElement.classList.contains("formError")) {
            confirmPasswordElement.classList.remove("formError");
        }
        confirmPasswordElement.classList.remove("formError")
        confirmPasswordErrorElement.innerText = null
    } else {
        confirmPasswordElement.classList.add("formError")
        confirmPasswordErrorElement.innerText = "Passwords do not match."
    }
}

/**
 * Check that the bio length is below 250 characters and
 * update the character count message below the bio text area.
 */
function checkBio() {
    let bioElement = document.getElementById("bio");
    let bioErrorElement = document.getElementById("bioError");
    let charMessage = document.getElementById("charCount");
    let charCount = bioElement.value.length;
    charMessage.innerText = charCount + ' '

    if (charCount > 250)
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
