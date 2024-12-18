package com.springboot.file_service.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import com.springboot.file_service.dto.request.*;
import com.springboot.file_service.dto.response.ApiResponse;
import com.springboot.file_service.entity.FileRelationship;
import com.springboot.file_service.exception.AppException;
import com.springboot.file_service.exception.ErrorCode;

import com.springboot.file_service.repository.IFileRelationshipRepository;
import com.springboot.file_service.repository.httpclient.ExamClient;
import com.springboot.file_service.utils.Constants;
import com.springboot.file_service.utils.EnumParentFileType;
import com.springboot.file_service.utils.Extensions;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@ExtensionMethod(Extensions.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {

    Drive googleDrive;
    IdentityService identityService;

    IFileRelationshipRepository fileRelationshipRepository;
    private final ExamClient examClient;

    public ApiResponse<?> importQuestionsIntoQuestionGroup(MultipartFile file, Long questionGrId) {

        return examClient.importQuestionsIntoQuestionGroup(file, questionGrId);
    }

    public ApiResponse<?> importStudentsIntoSubject(MultipartFile file, Long questionId) {

        return examClient.importStudentsIntoSubject(file, questionId);
    }

    public ByteArrayInputStream exportStudentOfClass(ExportStudentOfClassRequest request, String typeExport) {
        log.info("ExportStudentOfClass");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if ("excel".equalsIgnoreCase(typeExport)) {
            log.info("Exporting Student Of Class Excel");
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("List student of class");
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("Student ID");
                row.createCell(1).setCellValue("Name");
                row.createCell(2).setCellValue("Email");
                row.createCell(3).setCellValue("Login Name");
                int dataRowIndex = 1;

                for (UserRequest userRequest : request.getUserRequests()) {
                    log.info(userRequest.getDisplayName());
                    log.info(userRequest.getEmailAddress());
                    log.info(userRequest.getLoginName());
                    log.info(userRequest.getId());
                    Row dataRow = sheet.createRow(dataRowIndex);
                    dataRow.createCell(0).setCellValue(userRequest.getId());
                    dataRow.createCell(1).setCellValue(userRequest.getDisplayName());
                    dataRow.createCell(2).setCellValue(userRequest.getEmailAddress());
                    dataRow.createCell(3).setCellValue(userRequest.getLoginName());
                    dataRowIndex++;
                }

                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        }
        return null;
    }

    private File sendFileToGoogleDrive(MultipartFile fileToUpload) {
        try {
            if (null != fileToUpload) {
                File fileMetadata = new File();
                fileMetadata.setParents(Collections.singletonList(Constants.FOLDER_TO_UPLOAD));
                fileMetadata.setName(ObjectId.get().toString());
                File uploadFile = googleDrive
                        .files()
                        .create(fileMetadata,
                                new InputStreamContent(fileToUpload.getContentType(),
                                        new ByteArrayInputStream(fileToUpload.getBytes()))
                        )
                        .setFields("id, size, mimeType, webViewLink, videoMediaMetadata, webContentLink").execute();
                googleDrive.permissions().create(uploadFile.getId(), getPermission()).execute();
                return uploadFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPathFile(String fileId, String parentType) {
        if (!parentType.isBlankOrNull() && !fileId.isBlankOrNull()) {
            switch (EnumParentFileType.valueOf(parentType)) {
                // Case 1 - Video
                case COURSE_VIDEO:
                    return Constants.BASE_VIDEO_URL + fileId + "/preview";
                // Case 2 - Image
                case QUESTION_IMAGE:
                case USER_AVATAR:
                    return Constants.BASE_IMAGE_URL + fileId;

                default:
                    return null;
            }
        }
        return null;
    }

    public List<FileRelationshipDTO> getFileRelationshipsByParentIdsAndType(List<String> parentIds, String type) {
        List<FileRelationship> fileRelationships = fileRelationshipRepository.findAllByParentIdInAndParentType(parentIds, type);
        return toDTOS(fileRelationships);
    }

    public List<FileRelationshipDTO> getFileRelationshipsByParentIds(List<String> parentIds) {
        List<FileRelationship> fileRelationships = fileRelationshipRepository.findAllByParentIdIn(parentIds);
        return toDTOS(fileRelationships);
    }

    public List<FileRelationshipDTO> getAllFile() {
        List<FileRelationship> fileRelationships = fileRelationshipRepository.findAll();
        return toDTOS(fileRelationships);
    }

    public Map<String, String> getUrlOfFile(List<FileRelationshipDTO> fileRelationshipDTOS) {
        Map<String, String> map = new HashMap<>();
        for (FileRelationshipDTO fileRelationshipDTO : fileRelationshipDTOS) {
            if (!fileRelationshipDTO.getPathFile().isBlankOrNull()) {
                map.put(fileRelationshipDTO.getParentId(), fileRelationshipDTO.getPathFile());
            }
        }
        return map;
    }

    public void deleteFileToGoogleDrive(String fileId) throws Exception {
        googleDrive.files().delete(fileId).execute();
    }

    public void deleteFile(String id) {
        Optional<FileRelationship> fileRelationship = fileRelationshipRepository.findById(id);
        if (fileRelationship.isEmpty()) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        try {
            deleteFileToGoogleDrive(fileRelationship.get().getFileId());
        } catch (Exception ignored) {
        }
        fileRelationshipRepository.deleteById(id);
    }

    public void deleteFileByPathFile(String pathFile) {
        FileRelationship fileRelationship = fileRelationshipRepository.findByPathFile(pathFile);
        if (fileRelationship == null) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        fileRelationshipRepository.deleteByPathFile(pathFile);
    }

    public List<FileRelationshipDTO> saveFiles(FileUploadRequest fileUploadRequest) {

        List<FileRelationshipDTO> results = new ArrayList<>();
        for (FileUploadRequest.FileUploadRequestDto dto : fileUploadRequest.getFiles()) {
            results.add(saveFile(dto.getFile(), dto.getParentId(), dto.getParentType()));
        }
        return results;
    }

    public FileRelationshipDTO saveFile(MultipartFile multipartFile, String parentId, String parentType) {

        String userId = identityService.getCurrentUser().getId();
        if (parentType == EnumParentFileType.USER_AVATAR.name()) {
            String idImage = fileRelationshipRepository.findAllByParentIdAndParentType(userId, parentType).getFileId();
            deleteFile(idImage);
        }

        File fileDrive = sendFileToGoogleDrive(multipartFile);
        if (fileDrive == null) {
            throw new AppException(ErrorCode.UPLOAD_FILE_NOT_SUCCESS);
        }
        FileRelationship fileRelationship = buildFileDriveToFileRelationship(fileDrive);
        fileRelationship.setParentId(parentId);
        fileRelationship.setParentType(parentType);
        fileRelationship.setPathFile(getPathFile(fileDrive.getId(), parentType));
        fileRelationship.setName(multipartFile.getOriginalFilename());
        fileRelationship.setCreatedAt(new Date());
        fileRelationship.setCreatedBy(userId);
        FileRelationship fileRelationshipSaved = fileRelationshipRepository.save(fileRelationship);
        return toDTO(fileRelationshipSaved);

    }

    public FileRelationship buildFileDriveToFileRelationship(File fileDrive) {
        if (fileDrive == null) return new FileRelationship();
        return FileRelationship.builder()
                .fileId(fileDrive.getId() != null ? fileDrive.getId() : null)
                .name(fileDrive.getName() != null ? fileDrive.getName() : null)
                .size(fileDrive.getSize() != null ? fileDrive.getSize() : null)
                .mimeType(fileDrive.getMimeType() != null ? fileDrive.getMimeType() : null)
                .webViewLink(fileDrive.getWebViewLink() != null ? fileDrive.getWebViewLink() : null)
                .duration(fileDrive.getVideoMediaMetadata() != null
                        && fileDrive.getVideoMediaMetadata().getDurationMillis() != null ? fileDrive.getVideoMediaMetadata().getDurationMillis() : null)
                .build();
    }

    private Permission getPermission() {
        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("reader");
        return permission;
    }

    public List<FileRelationshipDTO> toDTOS(List<FileRelationship> entities) {
        if (entities.isNullOrEmpty()) return new ArrayList<>();
        List<FileRelationshipDTO> dtos = new ArrayList<>();
        for (FileRelationship entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    public FileRelationshipDTO toDTO(FileRelationship entity) {
        if (entity == null) return new FileRelationshipDTO();
        return FileRelationshipDTO.builder()
                .id(entity.getId())
                .parentId(entity.getParentId())
                .parentType(entity.getParentType())
                .fileId(entity.getFileId())
                .mimeType(entity.getMimeType())
                .name(entity.getName())
                .size(entity.getSize())
                .duration(entity.getDuration())
                .webViewLink(entity.getWebViewLink())
                .pathFile(entity.getPathFile())
                .build();
    }

    public String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }

        return sb.toString();
    }

    public ByteArrayInputStream exportStudentsVerified(List<UserRequest> dataExport, String typeExport) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if ("excel".equalsIgnoreCase(typeExport)) {
            log.info("Exporting Student Verified Excel");
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("List student verified");
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("Student ID");
                row.createCell(1).setCellValue("Name");
                row.createCell(2).setCellValue("Email");
                row.createCell(3).setCellValue("Login Name");
                int dataRowIndex = 1;

                for (UserRequest userRequest : dataExport) {
                    log.info(userRequest.getDisplayName());
                    log.info(userRequest.getEmailAddress());
                    log.info(userRequest.getLoginName());
                    log.info(userRequest.getId());
                    Row dataRow = sheet.createRow(dataRowIndex);
                    dataRow.createCell(0).setCellValue(userRequest.getId());
                    dataRow.createCell(1).setCellValue(userRequest.getDisplayName());
                    dataRow.createCell(2).setCellValue(userRequest.getEmailAddress());
                    dataRow.createCell(3).setCellValue(userRequest.getLoginName());
                    dataRowIndex++;
                }

                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        }
        return null;
    }

    public ByteArrayInputStream exportTrackEvent(List<TrackEventRequest> dataExport, String typeExport) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if ("excel".equalsIgnoreCase(typeExport)) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Log event");
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("Log event do exam");

                int dataRowIndex = 1;

                for (TrackEventRequest eventRequest : dataExport) {

                    Row dataRow = sheet.createRow(dataRowIndex);
                    dataRow.createCell(0).setCellValue(eventRequest.toString());

                    dataRowIndex++;
                }

                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        }
        return null;
    }
}
