/**
 * Script for creating, updating and deleting user profile image
 */
let croppie;
let image;
let fileName;
const validTypes = ["image/jpeg", "image/gif", "image/png"]
/**
 * Updates the image displayed to the user, creates a Croppie object for cropping and updates the deleteImage element
 * @param event
 */
async function loadFile (event) {
    console.log(event.target.files[0].type);
    if (validTypes.includes(event.target.files[0].type)) {
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
    } else {
        document.getElementById("image").value = "";
    }
}

/**
 * Obtains the result of the cropped image and updates the current image file to the cropped image
 */
function cropImage() {
    if (fileName != undefined) {
        const imageType = fileName.split(".")[1]
        croppie.result({type:'blob', format: imageType, circle:'true'})
        .then(function (imgBlob) {
            image.src = URL.createObjectURL(imgBlob)
            const file = new File([imgBlob], fileName,{type:imgBlob.type, lastModified:new Date().getTime()}, 'utf-8');
            const container = new DataTransfer();
            container.items.add(file);
            document.getElementById('image').files = container.files;
        })
    }

};

/**
 *  Function to delete the user profile image, set it to default image and also set the deleteImage input tag to true
 */
function deleteProfilePhoto() {
    let profilePhoto = document.getElementById("output")
    let deleteImage = document.getElementById("deleteImage");
    profilePhoto.src = "/icons/default-image.svg";
    deleteImage.value = true;
    let imageInput = document.getElementById("image")
    imageInput.value = "";

}


