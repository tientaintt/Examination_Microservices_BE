package com.spring.boot.exam_service.service.impl;


import com.spring.boot.exam_service.constants.Constants;
import com.spring.boot.exam_service.constants.ErrorMessage;
import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateQuestionGroupDTO;
import com.spring.boot.exam_service.dto.request.UpdateQuestionGroupDTO;
import com.spring.boot.exam_service.dto.response.QuestionGroupResponse;
import com.spring.boot.exam_service.entity.Classroom;
import com.spring.boot.exam_service.entity.QuestionGroup;
import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.ClassroomRepository;
import com.spring.boot.exam_service.repository.QuestionGroupRepository;
import com.spring.boot.exam_service.repository.QuestionRepository;
import com.spring.boot.exam_service.service.QuestionGroupService;
import com.spring.boot.exam_service.utils.CustomBuilder;
import com.spring.boot.exam_service.utils.PageUtils;
import com.spring.boot.exam_service.utils.WebUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class QuestionGroupServiceImpl implements QuestionGroupService {
    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionRepository questionRepository;
    private final ClassroomRepository classroomRepository;
    private final WebUtils webUtils;
    private static final String CODE_PREFIX = "group_";
    @Override
    public ApiResponse<?> createQuestionGroup(CreateQuestionGroupDTO dto) {
//        log.info("Start create Question Group");
//        UserProfile userProfile = webUtils.getCurrentLogedInUser();
//        Optional<Classroom> classRoom =  classroomRepository.findById(dto.getClassroomId());
//
//        QuestionGroup questionGroup = new QuestionGroup();
//        questionGroup.setName(dto.getName().trim());
//        questionGroup.setDescription(dto.getDescription().trim());
//        questionGroup.setCode(CODE_PREFIX + dto.getCode().trim());
//        questionGroup.setCreatedBy(userProfile.getLoginName());
//        questionGroup.setClassRoom(classRoom.get());
//
//        questionGroup = questionGroupRepository.save(questionGroup);
//        QuestionGroupResponse response = CustomBuilder.buildQuestionGroupResponse(questionGroup);
//
//        log.info("End create Question Group");
        return ApiResponse.builder().build();
    }

    @Override
    public ApiResponse<?> getAllQuestionGroupOfClassroom(Long classroomId, String search, int page, String column, int size, String sortType, Boolean isEnable) {
        log.info("Start get all Question Group of classroom");
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        Optional<Classroom> classRoom =  classroomRepository.findById(classroomId);
        if (classRoom.isEmpty()){
            throw new AppException(ErrorCode.CLASSROOM_NOT_FOUND);
        }
        String searchText = "%" + search.trim() + "%";
        Page<QuestionGroup> questionGroups = questionGroupRepository
                .findQuestionGroupsOfClassroomByClassroomId(classroomId, searchText, isEnable, pageable);

        Page<QuestionGroupResponse> response = questionGroups.map(CustomBuilder::buildQuestionGroupResponse);
        for(QuestionGroupResponse group : response) {
            Long groupID = group.getId();
            Long totalQuestionInGr = questionRepository.countQuestionsByQuestionGroupId(groupID);
            group.setTotalQuestion(totalQuestionInGr);
        }
        log.info("End get all Question Group of classroom");
        return ApiResponse.builder()
                .data(response).build();
    }

    @Override
    public ApiResponse<?> switchQuestionGroupStatus(Long questionGroupId, boolean newStatus) {
        log.info("Start switch Question Group status to " + newStatus);

        Optional<QuestionGroup> questionGroup = questionGroupRepository.findById(questionGroupId);

        if (questionGroup.isEmpty()){
            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND_ERROR);
        }

        questionGroup.get().setIsEnable(newStatus);
        modifyUpdateQuestionGroup(questionGroup.get());
        questionGroupRepository.save(questionGroup.get());
        log.info("End switch Question Group status to " + newStatus);
        return ApiResponse.builder().build();
    }

    private void modifyUpdateQuestionGroup(QuestionGroup questionGroup) {
//        UserProfile userProfile = webUtils.getCurrentLogedInUser();
//        questionGroup.setUpdateBy(userProfile.getLoginName());
//        questionGroup.setUpdateDate(Instant.now());
    }

    @Override
    public ApiResponse<?> updateQuestionGroup(Long questionGroupId, UpdateQuestionGroupDTO dto) {
        log.info("Start update Question Group");
        Optional<QuestionGroup> questionGroupOp = questionGroupRepository.findById(questionGroupId);
        if (questionGroupOp.isEmpty()){
            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND_ERROR);
        }
        QuestionGroup questionGroup = questionGroupOp.get();
        if (StringUtils.isNoneBlank(dto.getName())){
            questionGroup.setName(dto.getName());
            modifyUpdateQuestionGroup(questionGroup);
        }
        if (StringUtils.isNoneBlank(dto.getDescription())){
            questionGroup.setDescription(dto.getDescription());
            modifyUpdateQuestionGroup(questionGroup);
        }
        if (StringUtils.isNoneBlank(dto.getCode())){
            Optional<QuestionGroup> questionGroupEx = questionGroupRepository.findByCode(dto.getCode().trim());
            if (questionGroupEx.isPresent() && questionGroupEx.get().getId() != questionGroupId){
//                LinkedHashMap<String, String> response = new LinkedHashMap<>();
//                response.put(Constants.ERROR_CODE_KEY, ErrorMessage.QUESTION_GROUP_CODE_DUPLICATE.getErrorCode());
//                response.put(Constants.MESSAGE_KEY, ErrorMessage.QUESTION_GROUP_CODE_DUPLICATE.getMessage());
//                return ApiResponse.status(HttpStatus.BAD_REQUEST)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(response);
                throw new AppException(ErrorCode.QUESTION_GROUP_CODE_DUPLICATE);
            }
            questionGroup.setCode(dto.getCode());
            modifyUpdateQuestionGroup(questionGroup);
        }
        questionGroup = questionGroupRepository.save(questionGroup);
        QuestionGroupResponse response = CustomBuilder.buildQuestionGroupResponse(questionGroup);
        log.info("End update Question Group");
        return ApiResponse.builder().data(response).build();
    }
}
