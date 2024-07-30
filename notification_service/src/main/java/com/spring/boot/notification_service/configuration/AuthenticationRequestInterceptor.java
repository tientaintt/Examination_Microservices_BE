package com.spring.boot.notification_service.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// @Component if config all request
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    // modify request before send
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes servletRequestAttributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        var authHeader=servletRequestAttributes.getRequest().getHeader("Authorization");

        //
        if(StringUtils.hasText(authHeader)){
            requestTemplate.header("Authorization",authHeader);
        }
    }
}
