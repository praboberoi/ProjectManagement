// let compressedImageBlob;
// let imgToCompress;

var loadFile = async function (event) {
    var image = document.getElementById("output");
    image.src = URL.createObjectURL(event.target.files[0]); // the link:
    var vanilla = new Croppie(image, {
        viewport: { width: 100, height: 100 },
        boundary: { width: 300, height: 300 },
        showZoomer: false,
        enableOrientation: true
    });
    vanilla.bind({
        url: image.src,
        orientation: 4
    });
//on button click
    vanilla.result('blob').then(function(blob) {
        // do something with cropped blob
    });
    // imgToCompress = image;
    //
    // const imageSize = event.target.files[0].size;
    // const MAX_IMAGE_SIZE = 1000000;
    // const compressionCoeff = MAX_IMAGE_SIZE / imageSize;
    //
    // await compressImage(image, 0.8, compressionCoeff)
    // console.log("OK")
    //
    // console.log(compressedImageBlob)
    // console.log(typeof compressedImageBlob)

    const reader = new FileReader();
    reader.onload = function (e) {
        document.getElementById("output").setAttribute("src", e.target.result);
    }

    reader.readAsDataURL(event.target.files[0]);

};


// const compressImage = async (image, resizingFactor, quality) => {
//     // resizing the image
//     const canvas = document.createElement("canvas");
//     const context = canvas.getContext("2d");
//
//     const originalWidth = imgToCompress.width;
//     const originalHeight = imgToCompress.height;
//
//     const canvasWidth = originalWidth * resizingFactor;
//     const canvasHeight = originalHeight * resizingFactor;
//
//     canvas.width = canvasWidth;
//     canvas.height = canvasHeight;
//
//     context.drawImage(
//         imgToCompress,
//         0,
//         0,
//         originalWidth * resizingFactor,
//         originalHeight * resizingFactor
//     );
//
//     // reducing the quality of the image
//     canvas.toBlob(
//         (blob) => {
//             if (blob) {
//                 // showing the compressed image
//                 compressedImageBlob = blob;
//                 imgToCompress.src = URL.createObjectURL(compressedImageBlob);
//                 image.innerHTML = bytesToSize(blob.size);
//             }
//         },
//         "image/jpeg",
//         quality
//     );
//
// }
//
// // source: https://stackoverflow.com/a/18650828
// function bytesToSize(bytes) {
//     var sizes = ["Bytes", "KB", "MB", "GB", "TB"];
//
//     if (bytes === 0) {
//         return "0 Byte";
//     }
//     const i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
//
//     return Math.round(bytes / Math.pow(1024, i), 2) + " " + sizes[i];
// }

