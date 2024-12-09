package com.spring.boot.chat_service.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChatRequest {
    private String message;
    private String sender;
    private String receiver;
}
