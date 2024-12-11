package com.spring.boot.identity_service.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.identity_service.dto.request.FileRequest;
import com.spring.boot.identity_service.dto.response.UserResponse;
import com.spring.boot.identity_service.repository.httpclient.FileClient;
import com.spring.boot.identity_service.util.JsonParserUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class FileService {
    FileClient fileClient;
    public FileRequest saveFile(MultipartFile file,String parentId,String parentType) {
        log.info("Saving file");
        log.info("File name: " + file.getOriginalFilename());
        log.info("File type: " + file.getContentType());
        log.info(parentId);
        log.info(parentType);
log.info(fileClient.saveFile(file,parentId,parentType).getData().toString());
        ObjectMapper objectMapper = new ObjectMapper();
        FileRequest fileRequest = new FileRequest();

        try {


            String json = objectMapper.writeValueAsString(fileClient.saveFile(file, parentId, parentType).getData());

            JsonNode jsonNode = objectMapper.readTree(json);

            fileRequest.setId(JsonParserUtils.getJsonStringValue(jsonNode, "id"));
            fileRequest.setName(JsonParserUtils.getJsonStringValue(jsonNode, "name"));
            fileRequest.setSize(JsonParserUtils.getJsonIntValue(jsonNode, "size"));
            fileRequest.setDuration(JsonParserUtils.getJsonStringValue(jsonNode, "duration"));
            fileRequest.setParent_id(JsonParserUtils.getJsonStringValue(jsonNode, "parent_id"));
            fileRequest.setParent_type(JsonParserUtils.getJsonStringValue(jsonNode, "parent_type"));
            fileRequest.setFile_id(JsonParserUtils.getJsonStringValue(jsonNode, "file_id"));
            fileRequest.setPath_file(JsonParserUtils.getJsonStringValue(jsonNode, "path_file"));
            fileRequest.setMime_type(JsonParserUtils.getJsonStringValue(jsonNode, "mime_type"));
            fileRequest.setWeb_view_link(JsonParserUtils.getJsonStringValue(jsonNode, "web_view_link"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileRequest;
    }


    public ResponseEntity<Resource> exportStudentsVerified(List<UserResponse> dataExport, String typeExport) {

        return fileClient.exportStudentsVerified(dataExport,typeExport);
    }
}
