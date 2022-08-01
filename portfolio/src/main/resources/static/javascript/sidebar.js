//Check if the sidebar is open, if it is close the sidebar when the hamburger menu is clicked
//Otherwise open the sidebar when the hamburger menu is clicked
function navToggle() {
    var checkSidebar = document.getElementById('sidebar');
    if (checkSidebar.style.display === 'block') {
        checkSidebar.style.display = 'none';
    } else {
        checkSidebar.style.display = 'block';
        checkSidebar.style.width = "175px";
    }
}
