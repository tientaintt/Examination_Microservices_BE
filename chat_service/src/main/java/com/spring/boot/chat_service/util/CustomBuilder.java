package com.spring.boot.chat_service.util;

import com.spring.boot.chat_service.dto.response.ChatMessageResponse;
import com.spring.boot.chat_service.entity.ChatMessage;

public class CustomBuilder {
    public static ChatMessageResponse buildChatMessageResponse(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .message(chatMessage.getMessage())
                .read(chatMessage.isRead())
                .createdAt(chatMessage.getCreatedAt())
                .senderId(chatMessage.getSenderId())
                .receiverId(chatMessage.getSenderId())
                .build();
    }
}
