//package nz.ac.canterbury.seng302.portfolio.service;
//
//import nz.ac.canterbury.seng302.portfolio.dto.ResponseMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//
//@Service
//public class WebSocketService {
//
//    private SimpMessagingTemplate messagingTemplate;
//
//    @Autowired
//    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    public void notifyFrontEnd(final String message) {
//          ResponseMessage response = new ResponseMessage(message);
//          messagingTemplate.convertAndSend("/topic/messages", response);
//    }
//}
