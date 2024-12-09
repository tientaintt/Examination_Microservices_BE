package com.spring.boot.chat_service.service;

import com.spring.boot.chat_service.entity.ChatRoom;

import java.util.Optional;

public interface ChatRoomService {
    Optional<String> getChatRoomId(String senderId,String receiverId, boolean createNewRoomIfNotExists);
}
