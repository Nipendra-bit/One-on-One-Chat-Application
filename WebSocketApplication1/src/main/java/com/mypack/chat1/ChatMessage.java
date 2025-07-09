package com.mypack.chat1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mypack.user.User;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatId;
    private String content;

    
    private String senderId;

    
    private String recipientId;
}
