// function navToggle() {
    // document.getElementById('sidenav-2').classList.toggle("open");
    // document.getElementById('sidenav-2').classList.toggle("")
// }

// const showSidebar = document.querySelector('.sidebar')
//
// showSidebar.onclick = function (){
//     menu.classlist.toggle("open")
// }

// const show = document.getElementsByClassName("sidenav")
//
// document.getElementsByClassName("btn-primary").onclick = function(){
//     menu.cl
// }


// const show = document.querySelector("button")
// const menu = document.getElementsByClassName("sidenav")
// const mee = document.getElementById("sidenav-1")
//
// show.addEventListener("click", toggle)

function navToggle() {
    var checkSidebar = document.getElementById('sidebar');
    if (checkSidebar.style.display === 'block') {
        checkSidebar.style.display = 'none';
    } else {
        document.getElementById('sidebar').style.display = 'block';
        document.getElementById('sidebar').style.width = "175px";
        console.log("open nav")
    }
    // const m = document.getElementsByClassName("sidenav").style.display="inline"
    // menu.classList.toggle("sidebar")
    // show.classList.toggle("sidebar")
    // mee.classList.toggle("sidebar")

}

// document.getElementById("hamburger-toggle").onclick = function() {
//     document.getElementById("sidebar").style.display = "block";
//     console.log("navbar showup")
// }