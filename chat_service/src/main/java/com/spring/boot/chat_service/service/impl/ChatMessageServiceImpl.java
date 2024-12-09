package com.spring.boot.chat_service.service.impl;

import com.spring.boot.chat_service.dto.request.ChatRequest;
import com.spring.boot.chat_service.dto.request.UserRequest;
import com.spring.boot.chat_service.dto.response.APIResponse;
import com.spring.boot.chat_service.dto.response.ChatMessageResponse;
import com.spring.boot.chat_service.dto.response.SenderMessageCount;
import com.spring.boot.chat_service.dto.response.SenderResponse;
import com.spring.boot.chat_service.entity.ChatMessage;
import com.spring.boot.chat_service.entity.ListWrapper;
import com.spring.boot.chat_service.repository.ChatMessageRepository;
import com.spring.boot.chat_service.service.ChatMessageService;
import com.spring.boot.chat_service.service.ChatRoomService;
import com.spring.boot.chat_service.service.IdentityService;
import com.spring.boot.chat_service.util.CustomBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {
    private final MongoTemplate mongoTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final IdentityService identityService;

    @Override
    public APIResponse<?> sendChatMessage(ChatRequest chatRequest) {
        Optional<String> roomId = chatRoomService.getChatRoomId(chatRequest.getSender(), chatRequest.getReceiver(), true);
        ChatMessage chatMessage = getChatMessage(chatRequest, roomId);
        chatMessage = chatMessageRepository.save(chatMessage);
        log.info(chatMessage.toString());
        messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getReceiverId(), chatMessage);
        return APIResponse.builder()
                .data(chatMessage)
                .build();
    }

    private static ChatMessage getChatMessage(ChatRequest chatRequest, Optional<String> roomId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(chatRequest.getSender());
        chatMessage.setReceiverId(chatRequest.getReceiver());
        chatMessage.setMessage(chatRequest.getMessage());
        chatMessage.setRoomId(roomId.get());
        return chatMessage;
    }

    @Override
    public APIResponse<?> getMessages(String senderId, String receiverId) {
        Optional<String> roomId = chatRoomService.getChatRoomId(senderId, receiverId, true);

        if (roomId.isPresent()) {

            List<ChatMessage> chatMessage = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId.get());
            List<ChatMessage> chatMessages = chatMessage.stream().map(chatMessage1 -> {
                chatMessage1.setRead(true);
                return chatMessage1;
            }).toList();
            log.info("roomId {}",roomId.get());
            chatMessageRepository.saveAll(chatMessages);
            List<ChatMessageResponse> responses=chatMessages.stream().map(CustomBuilder::buildChatMessageResponse).toList();
            return APIResponse.builder()
                    .data(responses)
                    .build();
        } else {
            return APIResponse.builder()
                    .data(new ArrayList<>())
                    .build();
        }
    }

    @Override
    public APIResponse<?> getUnreadMessagesByReceiverId(String receiverId, int page, int size) {
        List<UserRequest> allSender = identityService.getAllUser();
        Aggregation aggregation = newAggregation(
                match(Criteria.where("receiverId").is(receiverId).and("read").is(false)),
                group("senderId")
                        .count().as("unreadCount")
                        .max("createdAt").as("lastChatTime"),
                sort(Sort.by(Sort.Order.desc("lastChatTime"))),
                project("unreadCount", "lastChatTime").and("senderId").previousOperation()
        );

        List<SenderMessageCount> result = mongoTemplate.aggregate(aggregation, ChatMessage.class, SenderMessageCount.class).getMappedResults();

        Map<String, SenderResponse> responseMap = new LinkedHashMap<>();

        for (SenderMessageCount senderMessageCount : result) {
            String senderId = senderMessageCount.getSenderId();
            Optional<UserRequest> senderOptional = allSender.stream()
                    .filter(sender -> sender.getId().equals(senderId))
                    .findFirst();
            log.info("A {} {} {}",
                    senderMessageCount.getUnreadCount() != null ? senderMessageCount.getUnreadCount().toString() : "N/A",
                    senderMessageCount.getLastChatTime() != null ? senderMessageCount.getLastChatTime().toString() : "N/A",
                    senderMessageCount.getSenderId() != null ? senderMessageCount.getSenderId().toString() : "N/A"
            );

            senderOptional.ifPresent(sender -> {
                SenderResponse response = SenderResponse.builder()
                        .displayName(sender.getDisplayName())
                        .id(sender.getId())
                        .unreadCount(senderMessageCount.getUnreadCount())
                        .lastChatTime(senderMessageCount.getLastChatTime())
                        .build();
                log.info("B {}", response.getUnreadCount().toString());
                responseMap.put(senderId, response);
            });
        }


        for (UserRequest sender : allSender) {
            responseMap.putIfAbsent(sender.getId(), SenderResponse.builder()
                    .displayName(sender.getDisplayName())
                    .id(sender.getId())
                    .unreadCount(0L)
                    .lastChatTime(null)
                    .build());
        }
        List<SenderResponse> finalResult = responseMap.values().stream()
                .filter(senderResponse -> !senderResponse.getId().equals(receiverId)).
                toList();
        int totalItems = finalResult.size();

        int fromIndex = page * size;
        if (totalItems < fromIndex) {

            ListWrapper<SenderResponse> wrapper = ListWrapper.<SenderResponse>builder()
                    .currentPage(page)
                    .maxResult(totalItems)
                    .data(new ArrayList<>())
                    .totalPage((totalItems - 1) / size + 1)
                    .build();
            return APIResponse.builder().data(wrapper).build();
        } else {
            int toIndex = Math.min(fromIndex + size, totalItems);

            List<SenderResponse> pagedResult = finalResult.subList(fromIndex, toIndex);
            ListWrapper<SenderResponse> wrapper = ListWrapper.<SenderResponse>builder()
                    .currentPage(page)
                    .maxResult(totalItems)
                    .data(pagedResult)
                    .totalPage((totalItems - 1) / size + 1)
                    .build();
            return APIResponse.builder().data(wrapper).build();
        }


    }

//    @Override
//    public APIResponse<?> getUnreadMessagesByReceiverId(String receiverId, int page, int size) {
//        List<UserRequest> allSender=identityService.getAllUser();
//
//        Aggregation aggregation = newAggregation(
//                match(Criteria.where("receiverId").is(receiverId).and("read").is(false)),
//                group("senderId")
//                        .count().as("unreadCount")
//                        .max("createdAt").as("lastChatTime"),
//                sort(Sort.by(Sort.Order.desc("lastChatTime"))),
//                project("senderId", "unreadCount","lastChatTime")
//        );
//
//        List<SenderMessageCount> result = mongoTemplate.aggregate(aggregation, ChatMessage.class, SenderMessageCount.class).getMappedResults();
//
//
//        Map<String, SenderMessageCount> resultMap = new HashMap<>();
//        for (SenderMessageCount senderMessageCount : result) {
//            resultMap.put(senderMessageCount.getSenderId(), senderMessageCount);
//        }
//
//
//        // Sử dụng LinkedHashMap để duy trì thứ tự của allSender
//        Map<String, SenderResponse> responseMap = new LinkedHashMap<>();
//        for (UserRequest sender : allSender) {
//            SenderMessageCount senderMessageCount = resultMap.get(sender.getId());
//
//            SenderResponse response;
//            if (senderMessageCount == null) {
//                response = SenderResponse.builder()
//                        .displayName(sender.getDisplayName())
//                        .id(sender.getId())
//                        .unreadCount(0L)
//                        .build();
//            } else {
//                response = SenderResponse.builder()
//                        .displayName(sender.getDisplayName())
//                        .id(sender.getId())
//                        .unreadCount(senderMessageCount.getUnreadCount())
//                        .build();
//            }
//
//            responseMap.put(sender.getId(), response);
//        }
//
//        // Sắp xếp lại finalResult theo thứ tự của resultMap
//        List<SenderResponse> finalResult = new ArrayList<>();
//
//        // Duyệt qua resultMap để thêm vào finalResult theo thứ tự của resultMap
//        for (Map.Entry<String, SenderMessageCount> entry : resultMap.entrySet()) {
//            String senderId = entry.getKey();
//            SenderResponse response = responseMap.get(senderId);
//            if (response != null) {
//                finalResult.add(response);
//            }
//        }
//
//        // Sau đó thêm vào các phần tử còn lại từ responseMap mà chưa có trong resultMap
//        for (Map.Entry<String, SenderResponse> entry : responseMap.entrySet()) {
//            if (!resultMap.containsKey(entry.getKey())) {
//                finalResult.add(entry.getValue());
//            }
//        }
//
//        return APIResponse.builder().data(finalResult).build();
//    }
}
