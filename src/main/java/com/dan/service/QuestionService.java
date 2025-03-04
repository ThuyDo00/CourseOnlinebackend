package com.dan.service;

import com.dan.model.Lession;
import com.dan.model.Question;
import com.dan.model.dto.CreateQuestionForm;
import com.dan.model.dto.Question_Answer;
import com.dan.model.dto.UpdateQuestionForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {
    public Question createQuestion(Question question);

    public Question updateQuestion(Question question, Long id);

    public void deleteQuestion(Long id);

    public Question getQuestion(Long id);

    public Page<Question> getQuestionsByLession(Lession lession, Pageable pageable);

    public List<Question> getQuestionsByLessonId(Long lessonId);

    public void createQuestionsWithAnswers(CreateQuestionForm createQuestionForm);

    public Question updateQuestion(UpdateQuestionForm updateQuestionForm);
}
