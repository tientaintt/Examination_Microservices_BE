package com.spring.boot.notification_service.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Request Method: {}, URI: {}, Headers: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getHeaderNames());


        filterChain.doFilter(request, response);


        log.info("Response Status: {}", response.getStatus());
    }
}

