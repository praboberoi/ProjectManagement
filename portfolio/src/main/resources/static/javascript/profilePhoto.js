var loadFile = function (event) {
    var image = document.getElementById("output");
    image.src = URL.createObjectURL(event.target.files[0]);

    const reader = new FileReader();
    reader.onload = function (e) {
        document.getElementById("output").setAttribute("src", e.target.result);

    }

    reader.readAsDataURL(event.target.files[0]);

};
