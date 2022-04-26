var loadFile = function (event) {
    var image = document.getElementById("output");
    image.src = URL.createObjectURL(event.target.files[0]);
};

function deleteProfilePhoto() {
    httpRequest = new XMLHttpRequest();
    
    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                alert(httpRequest.responseText);
            } else {
                alert('There was a problem with the request.');
            }
        }
    }
    httpRequest.open('DELETE', "/deleteProfilePhoto");
    httpRequest.send();
}
