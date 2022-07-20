function validateForm() {
    const errors = document.getElementsByClassName("formError");
    console.log(errors)
    if (errors.length == 0) {
        return true;
    }
    return false;
}