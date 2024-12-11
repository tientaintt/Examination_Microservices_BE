package com.spring.boot.identity_service.repository.httpclient;

import com.spring.boot.identity_service.configuration.AuthenticationRequestInterceptor;
import com.spring.boot.identity_service.configuration.FeignSupportConfig;
import com.spring.boot.identity_service.dto.request.FileRequest;
import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@FeignClient(name = "file-service", url = "${app.services.file}", configuration = {AuthenticationRequestInterceptor.class, FeignSupportConfig.class})
public interface FileClient {
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    APIResponse<?> saveFile(@RequestPart MultipartFile file, @RequestParam String parentId, @RequestParam String parentType);

    @PostMapping(value = "/export/student_verified")
    ResponseEntity<Resource> exportStudentsVerified(@RequestBody List<UserResponse> dataExport, @RequestParam String typeExport);
}
