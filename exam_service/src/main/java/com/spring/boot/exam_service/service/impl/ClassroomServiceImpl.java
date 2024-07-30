package com.spring.boot.exam_service.service.impl;



import com.spring.boot.exam_service.dto.ApiResponse;

import com.spring.boot.exam_service.dto.request.AddToClassroomDTO;
import com.spring.boot.exam_service.dto.request.CreateClassroomDTO;
import com.spring.boot.exam_service.dto.request.UpdateClassroomDTO;
import com.spring.boot.exam_service.dto.request.UserRequest;
import com.spring.boot.exam_service.dto.response.ClassroomResponse;
import com.spring.boot.exam_service.entity.Classroom;

import com.spring.boot.exam_service.entity.ClassroomRegistration;
import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.ClassroomRegistrationRepository;
import com.spring.boot.exam_service.repository.ClassroomRepository;
import com.spring.boot.exam_service.repository.httpclient.IdentityClient;
import com.spring.boot.exam_service.service.ClassroomService;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.utils.CustomBuilder;
import com.spring.boot.exam_service.utils.PageUtils;
import com.spring.boot.exam_service.utils.WebUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {


    private final ClassroomRepository classRoomRepository;
    private final IdentityService  identityService;
    private final ClassroomRegistrationRepository classroomRegistrationRepository;
    private final IdentityClient identityClient;
    private static final String CODE_PREFIX = "classroom_";
    private final ClassroomRepository classroomRepository;

    @Override
    public ApiResponse<?> getNumberClassroomManager() {
        log.info("Get number classroom is being manage");
        UserRequest userProfile = identityService.getCurrentUser();
        int count = classroomRepository.countByUserProfile(userProfile.getId());
        Map<String, Integer> response = new HashMap<>();
        response.put("numberClassManage", count);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    /**
     * Create a new topic
     *
     * @param DTO : The DTO contains the data
     * @return : The {@link ClassroomResponse}
     */
    @Override
    public ApiResponse<?> createClassroom(CreateClassroomDTO DTO) {
        log.info("Start create Classroom");
        UserRequest userProfile = identityService.getCurrentUser();
        Classroom classRoom = new Classroom();
        classRoom.setClassName(DTO.getClassName().trim());
        classRoom.setClassCode(CODE_PREFIX + DTO.getClassCode().trim());
        classRoom.setDescription(DTO.getDescription().trim());
        classRoom.setIsPrivate(DTO.getIsPrivate());
        classRoom.setCreatedBy(userProfile.getLoginName());
        classRoom.setUserID(userProfile.getId());
        Classroom savedClassroom = classRoomRepository.save(classRoom);
        ClassroomResponse response = CustomBuilder.buildClassroomResponse(savedClassroom);
        log.info("End create Classroom");
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
    public ApiResponse<?> switchClassroomStatus(Long classroomID, Boolean newStatus) {
        log.info("Start switch Classroom status to " + newStatus);
        Optional<Classroom> value = classRoomRepository.findById(classroomID);
        if (value.isEmpty()){
           throw new AppException(ErrorCode.CLASSROOM_NOT_FOUND);
        }
        Classroom classRoom = value.get();
        classRoom.setIsEnable(newStatus);
        modifyUpdateClassroom(classRoom);
        classRoomRepository.save(classRoom);
        log.info("End switch Classroom status to " + newStatus);
        return ApiResponse.builder().build();
    }

    /**
     * Get all the enable Classroom
     *
     * @return the page of DTO response {@link ClassroomResponse}
     */
    @Override
    public ApiResponse<?> getAllClassroomsByStatus(String search,int page,String column,int size,String sortType, Boolean isEnable) {
        log.info("Start get all enable Classroom (non-Admin)");
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<Classroom> classRooms = classRoomRepository.findAllSearchedClassRoomsByStatus(searchText,isEnable, pageable);
        // Map topic to topic response DTO
        Page<ClassroomResponse> response = classRooms.map(CustomBuilder::buildClassroomResponse);
        response.forEach((item)->{
            Long total = classroomRegistrationRepository.countAllByClassRoomId(item.getId());
            item.setNumberOfStudents(total);
        });
        log.info("End get all enable Classroom (non-Admin)");
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    /**
     * Update modify information of Classroom
     *
     * @param classRoom the entity
     */
    private void modifyUpdateClassroom(Classroom classRoom) {
        UserRequest userProfile = identityService.getCurrentUser();
        classRoom.setUpdateBy(userProfile.getLoginName());
        classRoom.setUpdateDate(Instant.now());
    }


    @Override
    public ApiResponse<?> updateClassroom(Long classroomId, UpdateClassroomDTO DTO) {
        log.info("Start update Classroom");
        Optional<Classroom> value = classRoomRepository.findById(classroomId);
        if (value.isEmpty()){
            throw new AppException(ErrorCode.CLASSROOM_NOT_FOUND);
        }
        Classroom classRoom = value.get();
        if(Objects.nonNull(DTO.getIsPrivate())){
            modifyUpdateClassroom(classRoom);
            classRoom.setIsPrivate(DTO.getIsPrivate());
        }
        if (StringUtils.isNoneBlank(DTO.getClassName())){
            classRoom.setClassName(DTO.getClassName().trim());
            modifyUpdateClassroom(classRoom);
        }
        if (StringUtils.isNoneBlank(DTO.getDescription())){
            classRoom.setDescription(DTO.getDescription().trim());
            modifyUpdateClassroom(classRoom);
        }
        if(StringUtils.isNoneBlank(DTO.getClassCode())){
            Optional<Classroom> classroomEx = classRoomRepository.findByClassCode(DTO.getClassCode().trim());
            if(classroomEx.isPresent() && classroomEx.get().getId() != classroomId){
//                LinkedHashMap<String, String> response = new LinkedHashMap<>();
//                response.put(Constants.ERROR_CODE_KEY, ErrorMessage.CLASS_CODE_DUPLICATE.getErrorCode());
//                response.put(Constants.MESSAGE_KEY, ErrorMessage.CLASS_CODE_DUPLICATE.getMessage());
//                return ApiResponse.status(HttpStatus.BAD_REQUEST)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(response);
                throw new AppException(ErrorCode.CLASS_CODE_DUPLICATE_ERROR);
            }
            classRoom.setClassCode(DTO.getClassCode().trim());
            modifyUpdateClassroom(classRoom);
        }
        classRoom = classRoomRepository.save(classRoom);
        ClassroomResponse response = CustomBuilder.buildClassroomResponse(classRoom);
        log.info("End update Classroom");
        return ApiResponse.builder()
                .data(response)
                .build();


    }

    @Override
    public ApiResponse<?> addStudentToClassroom(AddToClassroomDTO dto) {
        Optional<Classroom> classRoom = classRoomRepository.findById(dto.getClassroomId());
        UserRequest userProfile = identityService.getUserVerifiedByIdAndStatus(dto.getStudentId(),true);

        Optional<ClassroomRegistration> classroomRegistrationExisted =
                classroomRegistrationRepository
                        .findByClassRoomIdAndUserID(classRoom.get().getId(), userProfile.getId());
        if(classroomRegistrationExisted.isPresent()){
            return ApiResponse.builder().build();
        }

        ClassroomRegistration classroomRegistration =
                ClassroomRegistration.builder()
                        .classRoom(classRoom.get())
                        .userID(userProfile.getId())
                        .build();
        ClassroomRegistration savedClassroomRegistration =
                classroomRegistrationRepository.save(classroomRegistration);
        classRoom.get().getClassroomRegistrations().add(savedClassroomRegistration);

        classRoomRepository.save(classRoom.get());

        return ApiResponse.builder().build();
    }

    @Override
    public ApiResponse<?> getAllStudentOfClassroom(Long classroomId, int page,String column,int size,String sortType) {
        log.info("Start get all user of classroom by id");
        Optional<Classroom> classRoom = classRoomRepository.findById(classroomId);
//        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        if (classRoom.isEmpty()){
            throw new AppException(ErrorCode.CLASSROOM_NOT_FOUND);
        }
        List<String> userIdOfClassroomId= classroomRepository.getAllUserIdOfClassroomByClassroomId(classroomId);
//        Page<UserProfile> listStudentByClassroom =
//                studentRepositoryRead.findAllStudentByClassroomId(classroomId, pageable);
//        Page<UserProfileResponse> response =
//                listStudentByClassroom.map(CustomBuilder::buildUserProfileResponse);

        log.info("End get all user of classroom by id");
        return identityClient.getAllUsersByUserIds(userIdOfClassroomId,page,column,size,sortType);
    }

    @Override
    public ApiResponse<?> getMyClassrooms(String search, int page, String column, int size, String sortType) {
        UserRequest userProfile = identityService.getCurrentUser();
        log.info("Get all my classroom. Start. User is: "+userProfile.getId());
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<Classroom> classRooms = classRoomRepository.findAllRegistedClassroomOfUser(userProfile.getId(),searchText, pageable);
        Page<ClassroomResponse> response = classRooms.map(CustomBuilder::buildClassroomResponse);
        response.forEach((item)->{
            Long total = classroomRegistrationRepository.countAllByClassRoomId(item.getId());
            item.setNumberOfStudents(total);
        });
        log.info("Get all my classroom. End. User is: "+userProfile.getId());
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> getClassRoomById(Long classroomId) {
        log.info("Get classroom by id. Start");
        Optional<Classroom> classroom = classRoomRepository.findActiveClassroomById(classroomId);
        if(classroom.isEmpty()) {
            throw new AppException(ErrorCode.CLASSROOM_NOT_FOUND);
        }
        ClassroomResponse response = CustomBuilder.buildClassroomResponse(classroom.get());
        Long total = classroomRegistrationRepository.countAllByClassRoomId(classroom.get().getId());
        response.setNumberOfStudents(total);
        log.info("Get classroom by id. End");
        return ApiResponse.builder()
                .data(response)
                .build();

    }

    @Override
    public ApiResponse<?> removeStudentFromClassroom(AddToClassroomDTO dto) {
        Optional<Classroom> classRoom = classRoomRepository.findById(dto.getClassroomId());
        UserRequest userProfile = identityService.getUserVerifiedByIdAndStatus(dto.getStudentId(), true);
        Optional<ClassroomRegistration> classroomRegistration = classroomRegistrationRepository.findByClassRoomIdAndUserID(classRoom.get().getId(), userProfile.getId());
        classroomRegistration.ifPresent(registration -> classroomRegistrationRepository.deleteById(registration.getId()));
        return ApiResponse.builder().build();
    }
}