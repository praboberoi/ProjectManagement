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

function deleteProfilePhoto() {
    let profilePhoto = document.getElementById("output")
    let deleteImage = document.getElementById("deleteImage");
    profilePhoto.src = "/icons/default-image.svg";
    deleteImage.value = true;
}

// function deleteProfilePhoto() {
//     httpRequest = new XMLHttpRequest();
//     let profilePhotoMessage = document.getElementById("profilePhotoMessage");
//
//     let newProfilePhoto = document.getElementById("output");
//     let profilePhotoSelector = document.getElementById("formFile");
//
//     httpRequest.onreadystatechange = function() {
//         if (httpRequest.readyState === XMLHttpRequest.DONE) {
//             if (httpRequest.status === 200 || newProfilePhoto.src != "/icons/default-image.svg") {
//                 newProfilePhoto.src = "/icons/default-image.svg";
//                 profilePhotoSelector.value = "";
//                 profilePhotoMessage.innerText = "Profile photo successfully removed.";
//                 profilePhotoMessage.classList.remove('error-msg');
//             } else if (httpRequest.status === 404) {
//                 profilePhotoMessage.innerText = "Profile photo not found.";
//                 profilePhotoMessage.classList.add('error-msg');
//             } else {
//                 profilePhotoMessage.innerText = "Something went wrong.";
//                 profilePhotoMessage.classList.add('error-msg');
//             }
//         }
//     }
//     httpRequest.open('DELETE', "/deleteProfilePhoto");
//     httpRequest.send();
// }

