package com.springboot.file_service.dto.request;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
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
    public static class FileUploadRequestDto {
        MultipartFile file;
        String parentId;
        String parentType;
    }
}

