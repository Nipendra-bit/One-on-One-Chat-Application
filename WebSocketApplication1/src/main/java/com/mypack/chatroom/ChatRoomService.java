package com.mypack.chatroom;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Service

public class ChatRoomService {
	@Autowired
	public  ChatRoomRepository chatroomrepository;

	
	public Optional<String> getChatRoomId( String senderId,String recipientId,boolean ChatRoomIfNotExists){
		return chatroomrepository.findBySenderIdAndRecipientId(senderId,recipientId)
				.map(ChatRoom::getChatId)
				.or(()->{
					if(ChatRoomIfNotExists) {
						String chatid=createChat(senderId,recipientId);
						return Optional.of(chatid);
					}
					return Optional.empty();
				});
	}
     
	public String createChat(String senderId, String recipientId) {
		String chatid=String.format("%s_%s", senderId,recipientId);
		
		ChatRoom senderRecipient=ChatRoom.builder()
				.chatId(chatid)
				.senderId(senderId)
				.recipientId(recipientId)
				.build();
		ChatRoom Recipientsender=ChatRoom.builder()
				.chatId(chatid)
				.senderId(recipientId)
				.recipientId(senderId)
				.build();
		chatroomrepository.save(senderRecipient);
		chatroomrepository.save(Recipientsender);
		return chatid;
	}

	

}
