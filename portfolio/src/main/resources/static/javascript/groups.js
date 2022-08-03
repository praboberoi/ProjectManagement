function checkShortName() {
    let groupShortNameElement = document.getElementById("shortName");

    let charMessage = document.getElementById("charCount");
    let charCount = groupShortNameElement.value.length;
    charMessage.innerText = charCount + ' '

}

function checkLongName() {
    let groupLongNameElement = document.getElementById("longName");

    let charMessage = document.getElementById("charCountLong");
    let charCount = groupLongNameElement.value.length;
    charMessage.innerText = charCount + ' '

}