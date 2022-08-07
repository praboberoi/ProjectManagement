/**
 * Count down the characters remaining in the Group Short name, and check the length is between 3 and 100 characters.
 */
function checkShortName() {
    let groupShortNameElement = document.getElementById("shortName");
    let groupShortNameErrorElement = document.getElementById("shortNameError")
    let charMessage = document.getElementById("charCount");
    let charCount = groupShortNameElement.value.length;
    charMessage.innerText = charCount + ' '
    if (charCount < 3 || charCount > 100) {
        groupShortNameElement.classList.add('formError');
        groupShortNameErrorElement.innerText = "Group Long Name must be between 3 and 100 characters."
        groupShortNameElement.setCustomValidity("Invalid Field")
    } else {
        groupShortNameElement.classList.remove("formError");
        groupShortNameErrorElement.innerText = null;
        groupShortNameElement.setCustomValidity("");
    }
}

/**
 * Count down the characters remaining in the Group Long name, and check the length is between 3 and 100 characters.
 */
function checkLongName() {
    let groupLongNameElement = document.getElementById("longName");
    let groupLongNameErrorElement = document.getElementById("longNameError")
    let charMessage = document.getElementById("charCountLong");
    let charCount = groupLongNameElement.value.length;
    if (charCount < 3 || charCount > 100) {
        groupLongNameElement.classList.add('formError');
        groupLongNameErrorElement.innerText = "Group Long Name must be between 3 and 100 characters."
        groupLongNameElement.setCustomValidity("Invalid Field")
    } else {
        groupLongNameElement.classList.remove("formError");
        groupLongNameErrorElement.innerText = null;
        groupLongNameElement.setCustomValidity("");
    }
    charMessage.innerText = charCount + ' '

}