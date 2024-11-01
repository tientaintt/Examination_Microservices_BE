package com.spring.boot.exam_service.configuration;

import com.spring.boot.exam_service.dto.request.UserRequest;
import com.spring.boot.exam_service.service.IdentityService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
@NoArgsConstructor(force = true)
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private final IdentityService identityService;

    @Autowired
    public AuditorAwareImpl(IdentityService identityService) {
        this.identityService = identityService;
    }
    @Override
    public Optional<String> getCurrentAuditor() {
        UserRequest currentUser= identityService.getCurrentUser();

        if (currentUser == null) {
            return Optional.of("ANONYMOUS_USER");
        }

        return Optional.of(currentUser.getId());
    }
}