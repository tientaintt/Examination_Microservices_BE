package com.spring.boot.chat_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageResponse {
    private Date createdAt;
    private String receiverId;
    private String message;
    private boolean read=false;
    private String senderId;
}
