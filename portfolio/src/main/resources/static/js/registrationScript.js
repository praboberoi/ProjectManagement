function check_pass() {
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