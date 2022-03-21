function match_pass() {
    let passwordElement = document.getElementById("password");
    let confirmPasswordElement = document.getElementById("confirmPassword");
    if (passwordElement.value ==
        confirmPasswordElement.value) {
        if (confirmPasswordElement.classList.contains("form_error")) {
            confirmPasswordElement.classList.remove("form_error");
        }
        document.getElementsByClassName('form_submit')[0].disabled = false;
    } else {
        confirmPasswordElement.classList.add("form_error");
        document.getElementsByClassName('form_submit')[0].disabled = true;
    }
}

function check_pass() {
    let passwordElement = document.getElementById("password");
    if (passwordElement.value < 8 || passwordElement.value > 63){
        passwordElement.classList.add("form_error")
    }
}

function check_username() {
    let passwordElement = document.getElementById("password");
    if (passwordElement.value < 3 || passwordElement.value > 63){
        passwordElement.classList.add("form_error")
    }
}