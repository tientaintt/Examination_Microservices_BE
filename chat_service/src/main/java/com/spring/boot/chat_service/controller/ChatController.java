package com.spring.boot.chat_service.controller;

import com.spring.boot.chat_service.dto.request.ChatRequest;
import com.spring.boot.chat_service.dto.response.APIResponse;
import com.spring.boot.chat_service.entity.ChatMessage;
import com.spring.boot.chat_service.service.ChatMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RestController
public class ChatController {
    ChatMessageService chatMessageService;

    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @PostMapping("/send")
    public APIResponse<?> sendMessage(@RequestBody ChatRequest chatRequest) {
        return chatMessageService.sendChatMessage(chatRequest);
    }

    @GetMapping(value = "/messages/{senderId}/{receiverId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public APIResponse<?> getMessages(@PathVariable("senderId") String senderId, @PathVariable("receiverId") String receiverId) {
        return  chatMessageService.getMessages(senderId,receiverId);
    }

    @GetMapping(value = "/senders/to/{receiverId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public APIResponse<?> getUnreadMessagesByReceiverId( @PathVariable("receiverId") String receiverId, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "0") int size) {
        return  chatMessageService.getUnreadMessagesByReceiverId(receiverId,page,size);
    }


}
