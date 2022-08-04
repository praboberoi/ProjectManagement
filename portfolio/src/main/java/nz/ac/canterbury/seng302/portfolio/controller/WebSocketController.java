//package nz.ac.canterbury.seng302.portfolio.controller;
//
//import nz.ac.canterbury.seng302.portfolio.dto.Message;
//import nz.ac.canterbury.seng302.portfolio.service.WebSocketService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//@Controller
//public class WebSocketController {
//
//    @Autowired
//    private WebSocketService webSocketService;
//
//    @PostMapping("/sendMessage")
//     public void sendMessage(@RequestBody final Message message) {
//        webSocketService.notifyFrontEnd(message.getMessageContent() );
//
//    }
//}
