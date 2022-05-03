 var loadFile = function(event) {
    var image = document.getElementById('output');
    image.src = URL.createObjectURL(event.target.files[0]);
};

    if(!(document.getElementById("output").files[0].type.match(/image.*/))){
        alert('You can\'t upload this type of file.');
        // return;
    }

