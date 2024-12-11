package com.spring.boot.exam_service.service.impl;



import com.spring.boot.event.dto.NotificationEvent;
import com.spring.boot.exam_service.dto.ApiResponse;

import com.spring.boot.exam_service.dto.request.*;
import com.spring.boot.exam_service.dto.response.SubjectResponse;
import com.spring.boot.exam_service.entity.*;

import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.MultipleChoiceTestRepository;
import com.spring.boot.exam_service.repository.SubjectRegistrationRepository;
import com.spring.boot.exam_service.repository.SubjectRepository;
import com.spring.boot.exam_service.repository.httpclient.FileClient;
import com.spring.boot.exam_service.repository.httpclient.IdentityClient;
import com.spring.boot.exam_service.service.SubjectService;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.utils.CustomBuilder;
import com.spring.boot.exam_service.utils.CustomPage;
import com.spring.boot.exam_service.utils.ExcelUtil;
import com.spring.boot.exam_service.utils.PageUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class SubjectServiceImpl implements SubjectService {


//    private final SubjectRepository subjectRepository;
    private final IdentityService  identityService;
    private final SubjectRegistrationRepository subjectRegistrationRepository;
    private final IdentityClient identityClient;
    private static final String CODE_PREFIX = "subject_";
    private final SubjectRepository subjectRepository;
    private  final FileClient fileClient;
    private final MultipleChoiceTestRepository multipleChoiceTestRepository;
    private final KafkaTemplate kafkaTemplate;

    @Override
    public ApiResponse<?> getNumberSubjectManager() {
        log.info("Get number subject is being manage");
        UserRequest userProfile = identityService.getCurrentUser();
        int count = subjectRepository.countByUserProfile(userProfile.getId());
        Map<String, Integer> response = new HashMap<>();
        response.put("numberSubjectManage", count);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    /**
     * Create a new topic
     *
     * @param DTO : The DTO contains the data
     * @return : The {@link SubjectResponse}
     */
    @Override
    public ApiResponse<?> createSubject(CreateSubjectDTO DTO) {
        log.info("Start create Subject");
        UserRequest userProfile = identityService.getCurrentUser();
        Subject subject = new Subject();
        subject.setSubjectName(DTO.getSubjectName().trim());
        subject.setSubjectCode(CODE_PREFIX + DTO.getSubjectCode().trim());
        subject.setDescription(DTO.getDescription().trim());
        subject.setIsPrivate(DTO.getIsPrivate());
        subject.setCreatedBy(userProfile.getId());
        subject.setUserID(userProfile.getId());
        Subject savedSubject = subjectRepository.save(subject);
        SubjectResponse response = CustomBuilder.buildSubjectResponse(savedSubject);
        log.info("End create Subject");
        return ApiResponse.builder()
                .data(response)
                .build();
    }


    /**
     * Change status of the topic by id
     *
     * @param subjectID : the topic id
     * @param newStatus : new boolean status
     * @return : no content response
     */
    @Override
    public ApiResponse<?> switchSubjectStatus(Long subjectID, Boolean newStatus) {
        log.info("Start switch Subject status to " + newStatus);
        Optional<Subject> value = subjectRepository.findById(subjectID);
        if (value.isEmpty()){
           throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        Subject subject = value.get();
        subject.setIsEnable(newStatus);
        modifyUpdateSubject(subject);
        subjectRepository.save(subject);
        log.info("End switch Subject status to " + newStatus);
        return ApiResponse.builder().build();
    }

    /**
     * Get all the enable Subject
     *
     * @return the page of DTO response {@link SubjectResponse}
     */
    @Override
    public ApiResponse<?> getAllSubjectsByStatus(String search,int page,String column,int size,String sortType, Boolean isEnable) {
        log.info("Start get all enable Subject (non-Admin)");
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<Subject> subjects = subjectRepository.findAllSearchedSubjectsByStatus(searchText,isEnable, pageable);
        // Map topic to topic response DTO
        Page<SubjectResponse> response = subjects.map(CustomBuilder::buildSubjectResponse);
        response.forEach((item)->{
            Long totalStudent = subjectRegistrationRepository.countAllBySubjectId(item.getId());
            Long totalExam = multipleChoiceTestRepository.countTestBySubjectId(item.getId());
            item.setNumberOfStudents(totalStudent);
            item.setNumberOfExams(totalExam);
        });
        log.info("End get all enable Subject (non-Admin)");
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    /**
     * Update modify information of Subject
     *
     * @param subject the entity
     */
    private void modifyUpdateSubject(Subject subject) {
        UserRequest userProfile = identityService.getCurrentUser();
        subject.setUpdateBy(userProfile.getId());
        subject.setUpdateDate(Instant.now());
    }


    @Override
    public ApiResponse<?> updateSubject(Long subjectId, UpdateSubjectDTO DTO) {
        log.info("Start update Subject");
        Optional<Subject> value = subjectRepository.findById(subjectId);
        if (value.isEmpty()){
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        Subject subject = value.get();
        if(Objects.nonNull(DTO.getIsPrivate())){
            modifyUpdateSubject(subject);
            subject.setIsPrivate(DTO.getIsPrivate());
        }
        if (StringUtils.isNoneBlank(DTO.getSubjectName())){
            subject.setSubjectName(DTO.getSubjectName().trim());
            modifyUpdateSubject(subject);
        }
        if (StringUtils.isNoneBlank(DTO.getDescription())){
            subject.setDescription(DTO.getDescription().trim());
            modifyUpdateSubject(subject);
        }
        if(StringUtils.isNoneBlank(DTO.getSubjectCode())){
            Optional<Subject> subjectEx = subjectRepository.findBySubjectCode(DTO.getSubjectCode().trim());
            if(subjectEx.isPresent() && subjectEx.get().getId() != subjectId){
//                LinkedHashMap<String, String> response = new LinkedHashMap<>();
//                response.put(Constants.ERROR_CODE_KEY, ErrorMessage.CLASS_CODE_DUPLICATE.getErrorCode());
//                response.put(Constants.MESSAGE_KEY, ErrorMessage.CLASS_CODE_DUPLICATE.getMessage());
//                return ApiResponse.status(HttpStatus.BAD_REQUEST)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(response);
                throw new AppException(ErrorCode.CLASS_CODE_DUPLICATE_ERROR);
            }
            subject.setSubjectCode(DTO.getSubjectCode().trim());
            modifyUpdateSubject(subject);
        }
        subject = subjectRepository.save(subject);
        SubjectResponse response = CustomBuilder.buildSubjectResponse(subject);
        Long totalStudent = subjectRegistrationRepository.countAllBySubjectId(response.getId());
        Long totalExam = multipleChoiceTestRepository.countTestBySubjectId(response.getId());
        response.setNumberOfStudents(totalStudent);
        response.setNumberOfExams(totalExam);
        log.info("End update Subject");
        return ApiResponse.builder()
                .data(response)
                .build();


    }

    @Override
    public ApiResponse<?> addStudentToSubject(AddToSubjectDTO dto) {
        log.info(dto.getStudentId().toString());
        Optional<Subject> subject = subjectRepository.findById(dto.getSubjectId());
        UserRequest userProfile = identityService.getUserVerifiedByIdAndStatus(dto.getStudentId(),true);

        Optional<SubjectRegistration> subjectRegistrationExisted =
                subjectRegistrationRepository
                        .findBySubjectIdAndUserID(subject.get().getId(), userProfile.getId());
        if(subjectRegistrationExisted.isPresent()){
            return ApiResponse.builder().build();
        }

        SubjectRegistration subjectRegistration =
                SubjectRegistration.builder()
                        .subject(subject.get())
                        .userID(userProfile.getId())
                        .build();
        SubjectRegistration savedSubjectRegistration =
                subjectRegistrationRepository.save(subjectRegistration);
        subject.get().getSubjectRegistrations().add(savedSubjectRegistration);

        Subject result=subjectRepository.save(subject.get());
        SubjectNotification subjectNotification=CustomBuilder.buildSubjectNotification(result);
        Map<String, Object> params = new HashMap<>();
        params.put("subjectNotification", subjectNotification);
        params.put("senderId",savedSubjectRegistration.getUserID());
        params.put("receiverId",userProfile.getId());
        NotificationEvent event = NotificationEvent.builder()
                .channel("")
                .templateCode("ADD_TO_SUBJECT")
                .param(params)
                .build();
        kafkaTemplate.send("notification-delivery",event);
        return ApiResponse.builder().build();
    }

    @Override
    public ApiResponse<?> addTeacherManageForSubject(AddManageForSubjectDTO dto) {

        Optional<Subject> subject = subjectRepository.findById(dto.getSubjectId());
        UserRequest userProfile = identityService.getUserVerifiedByIdAndStatus(dto.getTeacherId(),true);
        subject.get().setUserID(dto.getTeacherId());

        subjectRepository.save(subject.get());

        return ApiResponse.builder().build();
    }

    @Override
    public ApiResponse<?> getAllStudentOfSubject(Long subjectId, int page,String column,int size,String sortType,String search) {
        log.info("Start get all user of subject by id");
        Optional<Subject> subject = subjectRepository.findById(subjectId);
//        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        if (subject.isEmpty()){
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        List<String> userIdOfSubjectId= subjectRepository.getAllUserIdOfSubjectBySubjectId(subjectId);
//        Page<UserProfile> listStudentBySubject =
//                studentRepositoryRead.findAllStudentBySubjectId(subjectId, pageable);
//        Page<UserProfileResponse> response =
//                listStudentBySubject.map(CustomBuilder::buildUserProfileResponse);

        log.info("End get all user of subject by id");
        UserIdsDTO userIdsDTO= UserIdsDTO.builder().userIds(userIdOfSubjectId).build();

        Page<UserRequest> res=identityClient.getAllUsersByUserIds(userIdsDTO,page,column,size,sortType,search).getData();

        Pageable pageable =PageUtils.createPageable(page,size,sortType,column);
        Sort sort = Sort.by(column);
        CustomPage<UserRequest> customPage = new CustomPage<>(res.getContent(), pageable, res.getTotalElements(), sort);
        return ApiResponse.builder().data(customPage).build();
    }

    @Override
    public ApiResponse<?> getMySubjects(String search, int page, String column, int size, String sortType) {
        UserRequest userProfile = identityService.getCurrentUser();
        log.info("Get all my subject. Start. User is: "+userProfile.getId());
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<Subject> subjects = subjectRepository.findAllRegisteredSubjectOfUser(userProfile.getId(),searchText, pageable);
        subjects.stream().forEach(subject -> log.info(subject.toString()));
        Page<SubjectResponse> response = subjects.map(CustomBuilder::buildSubjectResponse);
        response.forEach((item)->{
            Long totalStudent = subjectRegistrationRepository.countAllBySubjectId(item.getId());
            Long totalExam = multipleChoiceTestRepository.countTestBySubjectId(item.getId());
            item.setNumberOfStudents(totalStudent);
            item.setNumberOfExams(totalExam);
        });
        log.info("Get all my subject. End. User is: "+userProfile.getId());
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> getAllSubjectManagementByIsPrivate(String search, int page, String column, int size, String sortType, boolean isPrivate) {
        UserRequest userProfile = identityService.getCurrentUser();
        log.info("Get all my subject management. Start. User is: "+userProfile.getId());
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<Subject> subjects = subjectRepository.findAllSubjectsByManagerId(userProfile.getId(),searchText,isPrivate,pageable);
        subjects.stream().forEach(subject -> log.info(subject.toString()));
        Page<SubjectResponse> response = subjects.map(CustomBuilder::buildSubjectResponse);
        response.forEach((item)->{
            Long totalStudent = subjectRegistrationRepository.countAllBySubjectId(item.getId());
            Long totalExam = multipleChoiceTestRepository.countTestBySubjectId(item.getId());
            item.setNumberOfStudents(totalStudent);
            item.setNumberOfExams(totalExam);
        });
        log.info("Get all my management. End. User is: "+userProfile.getId());
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> importStudentsIntoSubject(MultipartFile file, Long subjectId) {
        try {
            Optional<Subject> subjectOp = subjectRepository.findById(subjectId);
            if(subjectOp.isEmpty()) {
                throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
            }
            List<String> studentIds = this.readStudentIdsFromExcel(file.getInputStream());
            addStudentIdsToSubject(subjectId,studentIds);
            if (studentIds.isEmpty()) {
                throw new AppException(ErrorCode.DATA_IMPORT_ERROR);
            }
            return ApiResponse.builder()
                    .data(studentIds)
                    .build();
        } catch (IOException e) {
            throw new AppException(ErrorCode.CANNOT_READ_FILE);
        }


    }
    public List<String> readStudentIdsFromExcel(InputStream inputStream) {
        List<String> studentIds = new ArrayList<>();
        List<String> allStudentIds = identityService.getAllStudentId();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() >= 1) {
                    String studentId = ExcelUtil.getCellValueFromCell(row.getCell(0));
                    studentIds.add(studentId);
                }
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.CANNOT_READ_FILE);
        }
        List<String> missingIds = studentIds.stream()
                .filter(id -> !allStudentIds.contains(id))
                .collect(Collectors.toList());

        if (!missingIds.isEmpty()) {
            throw new AppException(ErrorCode.STUDENT_IDS_ERROR);
        }

        return studentIds;
    }
    public void addStudentIdsToSubject(Long subjectId, List<String> studentIds) {
        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
        Subject subject = optionalSubject.get();
        studentIds.forEach(studentId -> {

            Optional<SubjectRegistration> subjectRegistrationExisted =
                    subjectRegistrationRepository.findBySubjectIdAndUserID(subject.getId(), studentId);

            if (subjectRegistrationExisted.isPresent()) {
                return;
            }


            SubjectRegistration subjectRegistration = SubjectRegistration.builder()
                    .subject(subject)
                    .userID(studentId)
                    .build();

           SubjectRegistration savedSubjectRegistration=subjectRegistrationRepository.save(subjectRegistration);
            subject.getSubjectRegistrations().add(subjectRegistration);
            SubjectNotification subjectNotification=CustomBuilder.buildSubjectNotification(subject);
            Map<String, Object> params = new HashMap<>();
            params.put("subjectNotification", subjectNotification);
            params.put("senderId",savedSubjectRegistration.getUserID());
            params.put("receiverId",studentId);
            NotificationEvent event = NotificationEvent.builder()
                    .channel("")
                    .templateCode("ADD_TO_SUBJECT")
                    .param(params)
                    .build();
            kafkaTemplate.send("notification-delivery",event);
        });
        subjectRepository.save(subject);
    }

    @Override
    public ApiResponse<?> getSubjectById(Long subjectId) {
        log.info("Get subject by id. Start");
        Optional<Subject> subject = subjectRepository.findActiveSubjectById(subjectId);
        if(subject.isEmpty()) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        SubjectResponse response = CustomBuilder.buildSubjectResponse(subject.get());
        Long totalStudent = subjectRegistrationRepository.countAllBySubjectId(response.getId());
        Long totalExam = multipleChoiceTestRepository.countTestBySubjectId(response.getId());
        response.setNumberOfStudents(totalStudent);
        response.setNumberOfExams(totalExam);
        log.info("Get subject by id. End");
        return ApiResponse.builder()
                .data(response)
                .build();

    }

    @Override
    public ApiResponse<?> removeStudentFromSubject(AddToSubjectDTO dto) {
        Optional<Subject> subject = subjectRepository.findById(dto.getSubjectId());
        UserRequest userProfile = identityService.getUserVerifiedByIdAndStatus(dto.getStudentId(), true);
        Optional<SubjectRegistration> subjectRegistration = subjectRegistrationRepository.findBySubjectIdAndUserID(subject.get().getId(), userProfile.getId());
        subjectRegistration.ifPresent(registration -> subjectRegistrationRepository.deleteById(registration.getId()));
        SubjectNotification subjectNotification=CustomBuilder.buildSubjectNotification(subject.get());
        Map<String, Object> params = new HashMap<>();
        params.put("subjectNotification", subjectNotification);
        params.put("senderId",subject.get().getUserID());
        params.put("receiverId",userProfile.getId());
        NotificationEvent event = NotificationEvent.builder()
                .channel("")
                .templateCode("REMOVE_FROM_SUBJECT")
                .param(params)
                .build();
        kafkaTemplate.send("notification-delivery",event);
        return ApiResponse.builder().build();
    }

    @Override
    public ResponseEntity<Resource> exportStudentsOfSubject(Long subjectId, String typeExport) {
        log.info("Start get all user of subject by id");
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        if (subject.isEmpty()){
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        List<String> userIdOfSubjectId= subjectRepository.getAllUserIdOfSubjectBySubjectId(subjectId);
        userIdOfSubjectId.forEach(s -> log.info(s.trim()));
        if(userIdOfSubjectId.isEmpty()){

            throw new AppException(ErrorCode.NO_STUDENT_IN_CLASS);
        }
        UserIdsDTO userIdsDTO = UserIdsDTO.builder().userIds(userIdOfSubjectId).build();
       Page<UserRequest> userRequests= identityClient.getAllUsersByUserIds(userIdsDTO,0,"",1000,"","").getData();
       List<UserRequest> userRequestList=userRequests.stream().toList();
       ExportStudentOfSubjectRequest exportStudentOfSubjectRequest= ExportStudentOfSubjectRequest.builder().userRequests(userRequestList).className(subject.get().getSubjectName()).idClass(subject.get().getId()).build();
       return fileClient.exportStudentOfClass(exportStudentOfSubjectRequest,typeExport);

    }


}