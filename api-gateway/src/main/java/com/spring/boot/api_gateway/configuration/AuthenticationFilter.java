package com.spring.boot.api_gateway.configuration;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.api_gateway.dto.ApiResponse;
import com.spring.boot.api_gateway.service.IdentityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    IdentityService identityService;
    ObjectMapper objectMapper;

    @NonFinal
    private String[] publicEndpoint = {"/identity/auth/.*",
            "/identity/signup/student",
            "/identity/signup/teacher",
            "/identity/login",
            "/identity/refresh_token",
            "/identity/users/user/update",
            "/identity/password/reset/.*",
"/exam/subject",
"/exam/subject/.*",
            "/notify/password/.*",
            "/identity/users"};

    @NonFinal
    @Value("${app.app-prefix}")
    private String apiPrefix;

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        return Arrays.stream(publicEndpoint).anyMatch(s -> request.getURI().getPath().matches(apiPrefix+s));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("AuthenticationFilter");
        log.info(exchange.getRequest().getURI().getPath());
        if(isPublicEndpoint(exchange.getRequest())) {
            log.info("Public");
            return chain.filter(exchange);
        }
        //get token from authorization header
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader)) {
            log.info("Not authenticated");
            return unauthenticated(exchange.getResponse());
        }
        String authToken = authHeader.getFirst().replace("Bearer ", "");
        log.info("authToken: {}", authToken);
        //verify token
        //delegate identity service
        //flapMap get result of method
        return identityService.introspect(authToken).flatMap(introspectResponseApiResponse -> {
            log.info("introspectResponseApiResponse: {}", introspectResponseApiResponse.getData());
            if (introspectResponseApiResponse.getData().isValid()) {
                return chain.filter(exchange);
            } else {
                return unauthenticated(exchange.getResponse());
            }
        }).onErrorResume(throwable ->{
                log.error(throwable.getMessage());
                return unauthenticated(exchange.getResponse());
        }
        );


        //

//        return chain.filter(exchange);
    }

    // xep thu tu cua GlobalFilter, so cang nho thi thu tu cang lon
    @Override
    public int getOrder() {
        return -1;
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthenticated")
                .build();
        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));

    }
}
