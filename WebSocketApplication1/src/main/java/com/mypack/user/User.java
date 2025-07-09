package com.mypack.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mypack.chat1.ChatMessage;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    @Column(nullable = false, unique = true)
    private String nickName; // Now primary key

    @Enumerated(EnumType.STRING)
    private Status status;

    private String fullName;

    
}
