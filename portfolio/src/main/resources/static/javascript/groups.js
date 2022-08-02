function checkShortName() {
    let groupShortNameElement = document.getElementById("group-short-name");

    let charMessage = document.getElementById("charCount");
    let charCount = groupShortNameElement.value.length;
    charMessage.innerText = charCount + ' '

}

function checkLongName() {
    let groupLongNameElement = document.getElementById("group-long-name");

    let charMessage = document.getElementById("charCountLong");
    let charCount = groupLongNameElement.value.length;
    charMessage.innerText = charCount + ' '

}