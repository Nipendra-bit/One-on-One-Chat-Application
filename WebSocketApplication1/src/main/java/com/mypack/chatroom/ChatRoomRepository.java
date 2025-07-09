package com.mypack.chatroom;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>{

	Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);

}
