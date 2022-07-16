let croppie;
let image;
let fileName;
async function loadFile (event) {

    image = document.getElementById("output")
    image.src = URL.createObjectURL(event.target.files[0])
    fileName = event.target.files[0].name;
    document.getElementById('uploadImage').classList.add('ready');

    if (typeof croppie !== "undefined")
        croppie.destroy()

    croppie = new Croppie(document.getElementById('uploadImage'),{
        viewport: { width: 200, height: 200, type: 'circle'},
        boundary: { width: 300, height: 300 },
        showZoomer: false
    });
    croppie.bind({url: image.src})
    document.getElementById("deleteImage").value = false;
}

function cropImage() {
    const imageType = fileName.split(".")[1]
    croppie.result({type:'blob', format: imageType, circle:'true'})
        .then(function (imgBlob) {
            image.src = URL.createObjectURL(imgBlob)
            const file = new File([imgBlob], fileName,{type:imgBlob.type, lastModified:new Date().getTime()}, 'utf-8');
            const container = new DataTransfer();
            container.items.add(file);
            document.getElementById('image').files = container.files;
        })

};

/**
 *  Function to delete the user profile image, set it to default image and also set the deleteImage input tag to true
 */
function deleteProfilePhoto() {
    let profilePhoto = document.getElementById("output")
    let deleteImage = document.getElementById("deleteImage");
    profilePhoto.src = "/icons/default-image.svg";
    deleteImage.value = true;
    let imageInput = document.getElementById("formFile")
    imageInput.value = "";

}


