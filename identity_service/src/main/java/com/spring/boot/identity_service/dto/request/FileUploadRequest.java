package com.spring.boot.identity_service.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadRequest {
    List<FileUploadRequestDto> files;
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FileUploadRequestDto {
        MultipartFile file;
        String parentId;
        String parentType;
    }
}

