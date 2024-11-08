package com.spring.boot.notification_service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeFailureException;

import java.io.IOException;

@Configuration
@EnableWebSocketMessageBroker

@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//       config.setPathMatcher(new AntPathMatcher("."));
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering stomp endpoints");
//        registry.addEndpoint("/ws").setAllowedOrigins("*").setAllowedOriginPatterns("*").setHandshakeHandler((request, response, wsHandler, attributes) -> {
//            log.info("Handshake received2");
//            log.info(attributes.toString());
//
//            log.info(String.valueOf(wsHandler.supportsPartialMessages()));
//            log.info(response.getHeaders().toString());
//            return true;
//        });
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

}
