package com.springboot.file_service.service;

import com.springboot.file_service.dto.request.UserRequest;
import org.springframework.stereotype.Service;


public interface IdentityService {
    UserRequest getCurrentUser();
}
