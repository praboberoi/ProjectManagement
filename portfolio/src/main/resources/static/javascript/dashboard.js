// let stompClient = null
// console.log(apiPrefix)
function updateProjectDetails(projectId, projectName, apiPrefix) {
    if(apiPrefix === null)
        apiPrefix = ""
    document.getElementById('messageProject').innerText =  `Are you sure you want to delete ${projectName}`;
    document.getElementById('deleteProject').setAttribute('action', `${apiPrefix}/dashboard/deleteProject/${projectId}`);
}


// function connect() {
//     const socket  = new SockJS("/websocket");
//     stompClient  = Stomp.over(socket);
//     stompClient.connect({}, function (frame) {
//         console.log(`Connected: ${frame}`);
//         stompClient.subscribe(`${apiPrefix}/topic/messages`, function (message) {
//             showMessage(JSON.parse(message.body).content);
//         })
//     })
// }
//
// function showMessage(message) {
//     document.getElementById('messages').innerText = message;
// }
//
// function sendMessage() {
//     console.log("Sending message")
//     stompClient.send(`${apiPrefix}/sendMessage`, {}, JSON.stringify({'messageContent': 'This is a test'}));
// }
//
// connect()
