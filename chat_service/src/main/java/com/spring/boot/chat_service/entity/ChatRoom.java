package com.spring.boot.chat_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "chat_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatRoom extends AbstractEntity implements Serializable {
    private String roomId;
    private String senderId;
    private String receiverId;

}
