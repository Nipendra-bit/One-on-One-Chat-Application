package com.mypack.chat1;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mypack.chatroom.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Service

public class ChatService {
	@Autowired
	public ChatRepository chatrepository ;
	@Autowired
	public ChatRoomService chatRoomService;
	

	public ChatMessage save(ChatMessage chatMessage) {
	    String chatId = chatRoomService.getChatRoomId(
	        chatMessage.getSenderId().toString(),
	        chatMessage.getRecipientId().toString(),
	        true
	    ).orElseThrow();

	    chatMessage.setChatId(chatId);
	    return chatrepository.save(chatMessage);
	}

	
	
	public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(chatrepository::findByChatId).orElse(new ArrayList<>());
    }
	

}
