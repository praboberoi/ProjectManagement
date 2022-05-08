function validateForm() {
    errors = document.getElementsByClassName("formError");
    if (errors.length == 0) {
        return true;
    }
    return false;
}