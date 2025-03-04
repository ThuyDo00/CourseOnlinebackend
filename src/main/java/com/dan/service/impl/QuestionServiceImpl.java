package com.dan.service.impl;

import com.dan.model.Answer;
import com.dan.model.Lession;
import com.dan.model.Question;
import com.dan.model.dto.CreateQuestionForm;
import com.dan.model.dto.Question_Answer;
import com.dan.model.dto.UpdateQuestionForm;
import com.dan.repository.AnswerRepository;
import com.dan.repository.LessionRepository;
import com.dan.repository.QuestionRepository;
import com.dan.service.AnswerService;
import com.dan.service.QuestionService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private LessionRepository lessionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Override
    @Transactional
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question updateQuestion(Question question, Long id) {
        return questionRepository.findById(id).map(question1 -> {
            question1.setContent(question.getContent());
            question1.setLession(question.getLession());
            return questionRepository.save(question1);
        }).orElseThrow(() -> new RuntimeException("Question not found with id " + id));
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public Question getQuestion(Long id) {
        return questionRepository.findById(id).orElseThrow(() -> new RuntimeException("Question not found with id " + id));
    }

    @Override
    public Page<Question> getQuestionsByLession(Lession lession, Pageable pageable) {
        return questionRepository.findByLession(lession, pageable);
    }

    @Override
    public List<Question> getQuestionsByLessonId(Long lessonId) {
        return questionRepository.findByLessionId(lessonId);
    }

    @Override
    public void createQuestionsWithAnswers(CreateQuestionForm createQuestionForm) {
        Lession lesson = createQuestionForm.getLession();
        List<Question_Answer> questionAnswers = createQuestionForm.getQuestion_answers();

        // Verify lesson exists
        Lession existingLesson = lessionRepository.findById(lesson.getId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lesson.getId()));

        // Validate questions and answers
        validateQuestionsAndAnswers(questionAnswers);

        // Create questions and answers
        questionAnswers.forEach(qa -> {
            Question question = createQuestion(qa, existingLesson);
            createAnswers(qa.getAnswers(), question);
        });
    }

    private Question createQuestion(Question_Answer qa, Lession lesson) {
        Question question = new Question();
        question.setLession(lesson);
        question.setContent(qa.getQuestionContent());
        return questionRepository.save(question);
    }

    private void createAnswers(List<Answer> answers, Question question) {
        answers.forEach(answer -> {
            answer.setQuestion(question);
            answerService.createAnswer(answer);
        });
    }

    private void validateQuestionsAndAnswers(List<Question_Answer> questionAnswers) {
        if (questionAnswers.stream().anyMatch(qa ->
                StringUtils.isEmpty(qa.getQuestionContent()) ||
                        qa.getAnswers() == null ||
                        qa.getAnswers().isEmpty())) {
            throw new IllegalArgumentException("Invalid question or answer data");
        }

        // Ensure at least one correct answer per question
        questionAnswers.forEach(qa -> {
            if (qa.getAnswers().stream().noneMatch(Answer::isCorrect)) {
                throw new IllegalArgumentException("Each question must have at least one correct answer");
            }
        });
    }

    @Override
    public Question updateQuestion(UpdateQuestionForm updateQuestionForm) {
        // Find existing question
        Question question = questionRepository.findById(updateQuestionForm.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + updateQuestionForm.getQuestionId()));

        // Update question content
        question.setContent(updateQuestionForm.getQuestionContent());

        // Update answers (Assuming Answer is an entity with Question as a relation)
        List<Answer> newAnswers = updateQuestionForm.getAnswers();
        if (newAnswers != null) {
            for (Answer answer : newAnswers) {
                answer.setQuestion(question);
            }
            answerRepository.saveAll(newAnswers);
        }

        return questionRepository.save(question);
    }

}
