var loadFile = function (event) {
    var image = document.getElementById("output");
    image.src = URL.createObjectURL(event.target.files[0]);

    const reader = new FileReader();
    reader.onload = function (e) {
        document.getElementById("output").setAttribute("src", e.target.result);

    }


    // reader.addEventListener("load", () => {
    //
    //     console.log(reader.result);
    //     localStorage.setItem("recent", reader.result)
    // })
    //
    reader.readAsDataURL(event.target.files[0]);

};

// document.addEventListener("DOMContentLoaded", () =>{
//     const recentImageDataUrl = localStorage.getItem("recent");
//
//     if(recentImageDataUrl){
//         document.getElementById("output").setAttribute("src", recentImageDataUrl);
//
//     }
// })

// console.log(loadFile());
//
// document.querySelector('#formFile').addEventListener("change", function (){
//     console.log(this.files);
//
// })