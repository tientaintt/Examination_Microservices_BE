package com.spring.boot.chat_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SenderMessageCount {
    String senderId;
    Long unreadCount;
    Date lastChatTime;
}
