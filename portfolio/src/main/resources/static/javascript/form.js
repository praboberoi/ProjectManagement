function validateForm() {
    errors = document.getElementsByClassName("form_error");
    if (errors.length == 0) {
        return true;
    }
    return false;
}