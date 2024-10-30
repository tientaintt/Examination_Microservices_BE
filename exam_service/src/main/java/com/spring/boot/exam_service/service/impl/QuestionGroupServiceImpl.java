package com.spring.boot.exam_service.service.impl;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateQuestionGroupDTO;
import com.spring.boot.exam_service.dto.request.UpdateQuestionGroupDTO;
import com.spring.boot.exam_service.dto.request.UserRequest;
import com.spring.boot.exam_service.dto.response.AnswerResponse;
import com.spring.boot.exam_service.dto.response.QuestionGroupResponse;
import com.spring.boot.exam_service.dto.response.QuestionResponse;
import com.spring.boot.exam_service.entity.*;
import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.*;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.service.QuestionGroupService;
import com.spring.boot.exam_service.utils.CustomBuilder;
import com.spring.boot.exam_service.utils.ExcelUtil;
import com.spring.boot.exam_service.utils.PageUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionGroupServiceImpl implements QuestionGroupService {
    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    IdentityService identityService;
    private static final String CODE_PREFIX = "group_";
    private final AnswerRepository answerRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final AnswerQuestionRepository answerQuestionRepository;

    @Override
    public ApiResponse<?> createQuestionGroup(CreateQuestionGroupDTO dto) {
        log.info("Start create Question Group");
        UserRequest userProfile = identityService.getCurrentUser();
        Optional<Subject> classRoom = subjectRepository.findById(dto.getSubjectId());

        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setName(dto.getName().trim());
        questionGroup.setDescription(dto.getDescription().trim());
        questionGroup.setCode(CODE_PREFIX + dto.getCode().trim());
        questionGroup.setCreatedBy(userProfile.getLoginName());
        questionGroup.setSubject(classRoom.get());

        questionGroup = questionGroupRepository.save(questionGroup);
        QuestionGroupResponse response = CustomBuilder.buildQuestionGroupResponse(questionGroup);

        log.info("End create Question Group");
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> getAllQuestionGroupOfClassroom(Long subjectId, String search, int page, String column, int size, String sortType, Boolean isEnable) {
        log.info("Start get all Question Group of classroom");
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        Optional<Subject> classRoom = subjectRepository.findById(subjectId);
        if (classRoom.isEmpty()) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        String searchText = "%" + search.trim() + "%";
        Page<QuestionGroup> questionGroups = questionGroupRepository
                .findQuestionGroupsOfClassroomByClassroomId(subjectId, searchText, isEnable, pageable);

        Page<QuestionGroupResponse> response = questionGroups.map(CustomBuilder::buildQuestionGroupResponse);
        for (QuestionGroupResponse group : response) {
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

        if (questionGroup.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND_ERROR);
        }

        questionGroup.get().setIsEnable(newStatus);
        modifyUpdateQuestionGroup(questionGroup.get());
        questionGroupRepository.save(questionGroup.get());
        log.info("End switch Question Group status to " + newStatus);
        return ApiResponse.builder().build();
    }

    private void modifyUpdateQuestionGroup(QuestionGroup questionGroup) {
        UserRequest userProfile = identityService.getCurrentUser();
        questionGroup.setUpdateBy(userProfile.getLoginName());
        questionGroup.setUpdateDate(Instant.now());
    }

    @Override
    public ApiResponse<?> updateQuestionGroup(Long questionGroupId, UpdateQuestionGroupDTO dto) {
        log.info("Start update Question Group");
        Optional<QuestionGroup> questionGroupOp = questionGroupRepository.findById(questionGroupId);
        if (questionGroupOp.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND_ERROR);
        }
        QuestionGroup questionGroup = questionGroupOp.get();
        if (StringUtils.isNoneBlank(dto.getName())) {
            questionGroup.setName(dto.getName());
            modifyUpdateQuestionGroup(questionGroup);
        }
        if (StringUtils.isNoneBlank(dto.getDescription())) {
            questionGroup.setDescription(dto.getDescription());
            modifyUpdateQuestionGroup(questionGroup);
        }
        if (StringUtils.isNoneBlank(dto.getCode())) {
            Optional<QuestionGroup> questionGroupEx = questionGroupRepository.findByCode(dto.getCode().trim());
            if (questionGroupEx.isPresent() && questionGroupEx.get().getId() != questionGroupId) {
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

    @Override
    public ApiResponse<?> importQuestionsIntoQuestionGroup(MultipartFile file, long questionGroupId) {
        try {
            List<Question> questions=this.readQuestionsFromExcel(file.getInputStream(),questionGroupId);
            if(questions.isEmpty()){
                throw new AppException(ErrorCode.CANNOT_READ_FILE);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.CANNOT_READ_FILE);
        }

        return ApiResponse.builder()
                .build();
    }

    public List<Question> readQuestionsFromExcel(InputStream inputStream, Long questionGroupId) {
        List<Question> questions = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() >= 1) {
                    String questionText = ExcelUtil.getCellValueFromCell(row.getCell(0));
                    String questionType = ExcelUtil.getCellValueFromCell(row.getCell(1));
                    String answers = ExcelUtil.getCellValueFromCell(row.getCell(2));
                    String correctAnswer = ExcelUtil.getCellValueFromCell(row.getCell(3));

                    Optional<QuestionType> questionType1 = questionTypeRepository.findByTypeQuestion(questionType);
                    if (questionType1.isPresent()) {
                        Optional<QuestionGroup> questionGroup = questionGroupRepository.findById(questionGroupId);
                        if (questionGroup.isPresent()) {
                            Question question = Question.builder()
                                    .questionType(questionType1.get())
                                    .content(questionText)
                                    .questionGroup(questionGroup.get())
                                    .build();
                            Question finalQuestion = questionRepository.save(question);
                            List<String> listAnswerContent = Arrays.asList(answers.split(","));
                            List<String> listCorrectAnswer = Arrays.asList(correctAnswer.split(","));
                            listAnswerContent.forEach(answerContent -> {
                                Optional<Answer> answer1 = answerRepository.findByAnswer(answerContent);
                                if (answer1.isEmpty()) {
                                    Answer save = answerRepository.save(Answer.builder()
                                            .answer(answerContent)
                                            .build());
                                    listCorrectAnswer.forEach(correctAnswerContent -> {
                                        answerQuestionRepository.save(
                                                AnswerQuestion.builder()
                                                        .answer(save)
                                                        .question(finalQuestion)
                                                        .isCorrect(correctAnswerContent.equals(save.getAnswer()))
                                                        .build());
                                    });


                                } else {

                                    listCorrectAnswer.forEach(correctAnswerContent -> {
                                        answerQuestionRepository.save(
                                                AnswerQuestion.builder()
                                                        .answer(answer1.get())
                                                        .question(finalQuestion)
                                                        .isCorrect(correctAnswerContent.equals(answer1.get().getAnswer()))
                                                        .build());
                                    });
                                }

                            });

                            questions.add(finalQuestion);
                        };

                    } else {
                        throw new AppException(ErrorCode.QUESTION_TYPE_NOT_FOUND_ERROR);
                    }

                }

            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.CANNOT_READ_FILE);
        }

        return questions;
    }

    @Override
    public ResponseEntity<InputStreamResource> exportQuestionOfQuestionGroup(Long questionGroupId) {
        String fileName = questionGroupId + ".xlsx";
        ByteArrayInputStream inputStream = this.exportQuestionOfQuestionGroup(questionGroupId, "excel");
        InputStreamResource resource = new InputStreamResource(inputStream);
        ResponseEntity<InputStreamResource> response = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
        return response;
    }

    public List<QuestionResponse> getQuestionByIdQuestionGroup(Long questionGroupId) {
        log.info("getQuestionByIdQuestionGroup {}", questionGroupId);
        Pageable pageable = PageUtils.createPageable(0, 1000, "asc", "id");
        Optional<QuestionGroup> questionGroupOp =
                questionGroupRepository.findByIdAndStatus(questionGroupId, true);
        if (questionGroupOp.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND_ERROR);
        }
        Page<Question> questions = questionRepository
                .getQuestionsOfQuestionGroupByQuestionGroupId(questionGroupId, null, true, pageable);

        List<QuestionResponse> response = questions.stream().map(question -> {
            log.info("questionId +{}", question.getId().toString());
            return CustomBuilder.buildQuestionResponse(question, answerRepository.findListAnswerByIdQuestion(question.getId()));
        }).toList();
        log.info("Get questions of question group: end, isActiveQuestion: " + true);
        return response;
    }

    public ByteArrayInputStream exportQuestionOfQuestionGroup(Long idQuestionGroup, String typeExport) {
        log.info("exportQuestionOfQuestionGroup");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if ("excel".equalsIgnoreCase(typeExport)) {
            log.info("Exporting exportQuestionOfQuestionGroup Excel");
            try (Workbook workbook = new XSSFWorkbook()) {
                log.info("Creating workbook");
                Sheet sheet = workbook.createSheet("List question");
                List<QuestionResponse> questionList = getQuestionByIdQuestionGroup(idQuestionGroup);

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Question");
                headerRow.createCell(1).setCellValue("Question type");
                headerRow.createCell(2).setCellValue("Answer");
                headerRow.createCell(3).setCellValue("Correct Answer");

                int i = 0;
                for (QuestionResponse question : questionList) {
                    log.info(question.getId().toString());
                    Row row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(question.getContent());
                    row.createCell(1).setCellValue(question.getQuestionType());
                    StringBuilder dapAnDung = new StringBuilder();
                    StringBuilder allAnswers = new StringBuilder();
                    for (AnswerResponse answer : question.getAnswers()) {
                        allAnswers.append(answer.getAnswerContent()).append(", ");
                        if (answer.getIsCorrect() == true) {
                            dapAnDung.append(answer.getAnswerContent()).append(", ");
                            ;
                        }
                    }
                    allAnswers.deleteCharAt(allAnswers.length() - 2); // Xóa dấu phẩy cuối cùng
                    dapAnDung.deleteCharAt(dapAnDung.length() - 2);
                    row.createCell(2).setCellValue(allAnswers.toString());
                    row.createCell(3).setCellValue(dapAnDung.toString());
                    i++;
                }
                for (int j = 0; j < 4; j++) {  // Điều chỉnh cho 4 cột
                    sheet.autoSizeColumn(j);
                }
                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            } catch (IOException e) {
                throw new AppException(ErrorCode.CANNOT_READ_FILE);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new AppException(ErrorCode.CANNOT_WRITE_FILE);
                }
            }


        }
        return null;
    }
}
