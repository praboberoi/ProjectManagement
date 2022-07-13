var loadFile = function (event) {
    var image = document.getElementById("output");
    image.src = URL.createObjectURL(event.target.files[0]);

    const reader = new FileReader();
    reader.onload = function (e) {
        document.getElementById("output").setAttribute("src", e.target.result);

    }

    reader.readAsDataURL(event.target.files[0]);
    let deleteImage = document.getElementById("deleteImage");
    deleteImage.value = false;

};

/**
 *  Function to delete the user profile image, set it to default image and also set the deleteImage input tag to true
 */
function deleteProfilePhoto() {
    let profilePhoto = document.getElementById("output")
    let deleteImage = document.getElementById("deleteImage");
    profilePhoto.src = "/icons/default-image.svg";
    deleteImage.value = true;
}


