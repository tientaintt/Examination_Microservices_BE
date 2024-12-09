package com.spring.boot.chat_service.service.impl;

import com.spring.boot.chat_service.entity.ChatRoom;
import com.spring.boot.chat_service.repository.ChatMessageRepository;
import com.spring.boot.chat_service.repository.ChatRoomRepository;
import com.spring.boot.chat_service.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;


    @Override
    public Optional<String> getChatRoomId(String senderId, String receiverId, boolean createNewRoomIfNotExists) {
        return chatRoomRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .map(r->r.getRoomId())
                .or(()->{
                    if(createNewRoomIfNotExists) {
                        String roomId=String.format("Room-%s-%s", senderId, receiverId);
                        ChatRoom senderRoom=ChatRoom.builder()
                                .roomId(roomId)
                                .senderId(senderId)
                                .receiverId(receiverId)
                                .build();
                        ChatRoom receiverRoom=ChatRoom.builder()
                                .roomId(roomId)
                                .senderId(receiverId)
                                .receiverId(senderId)
                                .build();
                        chatRoomRepository.save(senderRoom);
                        chatRoomRepository.save(receiverRoom);
                        return Optional.of(roomId);
                    }
                    return Optional.empty();
                });
    }
}
