package com.dan.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.dan.model.Answer;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionForm {
    private Long questionId;
    private String questionContent;
    private List<Answer> answers; // Replace with a proper DTO if needed
}
