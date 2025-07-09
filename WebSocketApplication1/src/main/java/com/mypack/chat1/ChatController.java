package com.mypack.chat1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.mypack.user.User;
import com.mypack.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
public class ChatController {
	
	@Autowired
	public ChatService chatService;
	@Autowired
	public SimpMessagingTemplate simpMessagingTemplate;

	   

	@MessageMapping("/chat")
	public void processMessage(@Payload ChatMessage chatMessage) {
	    

	    ChatMessage message = new ChatMessage();
	    message.setSenderId(chatMessage.getSenderId());
	    message.setRecipientId(chatMessage.getRecipientId());
	    message.setContent(chatMessage.getContent());

	    ChatMessage savedMsg = chatService.save(message);

	    simpMessagingTemplate.convertAndSendToUser(
	    		message.getRecipientId(), "/queue/messages",
	            new ChatNotification(
	                String.valueOf(savedMsg.getId()),
	                message.getSenderId(),
	                message.getRecipientId(),
	                savedMsg.getContent()
	        )
	    );
	}

	


	    @GetMapping("/messages/{senderId}/{recipientId}")
	    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId,
	                                                 @PathVariable String recipientId) {
	        return ResponseEntity
	                .ok(chatService.findChatMessages(senderId, recipientId));
	    }
	}


