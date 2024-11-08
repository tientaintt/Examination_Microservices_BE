package com.spring.boot.exam_service.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

import java.util.List;

@Setter
@Getter
@SuperBuilder
public class MultipleChoiceTestWithQuestionsResponse extends MultipleChoiceTestResponse {
    Page<QuestionResponse> questions;
}
