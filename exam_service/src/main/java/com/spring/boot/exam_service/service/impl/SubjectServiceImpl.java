package com.spring.boot.exam_service.service.impl;



import com.spring.boot.exam_service.dto.ApiResponse;

import com.spring.boot.exam_service.dto.request.*;
import com.spring.boot.exam_service.dto.response.SubjectResponse;
import com.spring.boot.exam_service.entity.Subject;

import com.spring.boot.exam_service.entity.SubjectRegistration;
import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.SubjectRegistrationRepository;
import com.spring.boot.exam_service.repository.SubjectRepository;
import com.spring.boot.exam_service.repository.httpclient.FileClient;
import com.spring.boot.exam_service.repository.httpclient.IdentityClient;
import com.spring.boot.exam_service.service.SubjectService;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.utils.CustomBuilder;
import com.spring.boot.exam_service.utils.CustomPage;
import com.spring.boot.exam_service.utils.PageUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

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
        subject.setCreatedBy(userProfile.getLoginName());
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
     * @param classroomID : the topic id
     * @param newStatus : new boolean status
     * @return : no content response
     */
    @Override
    public ApiResponse<?> switchSubjectStatus(Long classroomID, Boolean newStatus) {
        log.info("Start switch Subject status to " + newStatus);
        Optional<Subject> value = subjectRepository.findById(classroomID);
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
            Long total = subjectRegistrationRepository.countAllBySubjectId(item.getId());
            item.setNumberOfStudents(total);
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
            Optional<Subject> classroomEx = subjectRepository.findBySubjectCode(DTO.getSubjectCode().trim());
            if(classroomEx.isPresent() && classroomEx.get().getId() != subjectId){
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

        Optional<SubjectRegistration> classroomRegistrationExisted =
                subjectRegistrationRepository
                        .findBySubjectIdAndUserID(subject.get().getId(), userProfile.getId());
        if(classroomRegistrationExisted.isPresent()){
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

        subjectRepository.save(subject.get());

        return ApiResponse.builder().build();
    }

    @Override
    public ApiResponse<?> getAllStudentOfSubject(Long subjectId, int page,String column,int size,String sortType,String search) {
        log.info("Start get all user of classroom by id");
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

        log.info("End get all user of classroom by id");
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
        log.info("Get all my classroom. Start. User is: "+userProfile.getId());
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<Subject> subjects = subjectRepository.findAllRegisteredSubjectOfUser(userProfile.getId(),searchText, pageable);
        subjects.stream().forEach(classroom -> log.info(classroom.toString()));
        Page<SubjectResponse> response = subjects.map(CustomBuilder::buildSubjectResponse);
        response.forEach((item)->{
            Long total = subjectRegistrationRepository.countAllBySubjectId(item.getId());
            item.setNumberOfStudents(total);
        });
        log.info("Get all my classroom. End. User is: "+userProfile.getId());
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> getSubjectById(Long subjectId) {
        log.info("Get classroom by id. Start");
        Optional<Subject> classroom = subjectRepository.findActiveSubjectById(subjectId);
        if(classroom.isEmpty()) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        SubjectResponse response = CustomBuilder.buildSubjectResponse(classroom.get());
        Long total = subjectRegistrationRepository.countAllBySubjectId(classroom.get().getId());
        response.setNumberOfStudents(total);
        log.info("Get classroom by id. End");
        return ApiResponse.builder()
                .data(response)
                .build();

    }

    @Override
    public ApiResponse<?> removeStudentFromSubject(AddToSubjectDTO dto) {
        Optional<Subject> subject = subjectRepository.findById(dto.getSubjectId());
        UserRequest userProfile = identityService.getUserVerifiedByIdAndStatus(dto.getStudentId(), true);
        Optional<SubjectRegistration> classroomRegistration = subjectRegistrationRepository.findBySubjectIdAndUserID(subject.get().getId(), userProfile.getId());
        classroomRegistration.ifPresent(registration -> subjectRegistrationRepository.deleteById(registration.getId()));
        return ApiResponse.builder().build();
    }

    @Override
    public ResponseEntity<Resource> exportStudentsOfSubject(Long subjectId, String typeExport) {
        log.info("Start get all user of classroom by id");
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