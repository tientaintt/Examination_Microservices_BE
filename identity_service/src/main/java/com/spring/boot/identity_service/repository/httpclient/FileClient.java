package com.spring.boot.identity_service.repository.httpclient;

import com.spring.boot.identity_service.configuration.AuthenticationRequestInterceptor;
import com.spring.boot.identity_service.configuration.FeignSupportConfig;
import com.spring.boot.identity_service.dto.request.FileRequest;
import com.spring.boot.identity_service.dto.response.APIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", url = "${app.services.file}", configuration = {AuthenticationRequestInterceptor.class, FeignSupportConfig.class})
public interface FileClient {
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    APIResponse<?> saveFile(@RequestPart MultipartFile file, @RequestParam String parentId, @RequestParam String parentType);

}
