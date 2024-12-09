package com.spring.boot.chat_service.service;

import com.spring.boot.chat_service.dto.request.ChatRequest;
import com.spring.boot.chat_service.dto.response.APIResponse;
import com.spring.boot.chat_service.entity.ChatMessage;

public interface ChatMessageService {
    APIResponse<?> sendChatMessage(ChatRequest chatRequest);

    APIResponse<?> getMessages(String senderId, String receiverId);

    APIResponse<?> getUnreadMessagesByReceiverId(String receiverId, int page, int size);
}
