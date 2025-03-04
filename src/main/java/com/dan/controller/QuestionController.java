package com.dan.controller;

import com.dan.model.*;
import com.dan.model.dto.CreateQuestionForm;
import com.dan.model.dto.Question_Answer;
import com.dan.model.dto.UpdateQuestionForm;
import com.dan.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private LessionService lessionService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @PostMapping("/create-question")
    public ResponseEntity<?> createQuestion(@RequestBody CreateQuestionForm createQuestionForm) {
        try {
            if (createQuestionForm.getLession() == null || createQuestionForm.getQuestion_answers() == null
                    || createQuestionForm.getQuestion_answers().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid input data"));
            }

            questionService.createQuestionsWithAnswers(createQuestionForm);

            return ResponseEntity.ok().body(Map.of("success", true, "message", "Questions created successfully", "data", createQuestionForm));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "Internal server error"));
        }
    }

    @PutMapping("/update-question")
    public ResponseEntity<?> updateQuestion(@RequestBody UpdateQuestionForm updateQuestionForm) {
        try {
            // Validate input
            if (updateQuestionForm.getQuestionId() == null || updateQuestionForm.getQuestionContent() == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid input data"));
            }

            // Call service to update question
            Question updatedQuestion = questionService.updateQuestion(updateQuestionForm);

            return ResponseEntity.ok(Map.of("success", true, "message", "Question updated successfully", "data", updatedQuestion));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "Internal server error"));
        }
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<Question_Answer>> getQuestionsAndAnswersByLesson(@PathVariable Long lessonId) {
        try {
            List<Question> questions = questionService.getQuestionsByLessonId(lessonId);
            List<Question_Answer> response = questions.stream().map(question -> {
                List<Answer> answers = answerService.getAnswersByQuestionId(question.getId());
                Question_Answer questionAnswer = new Question_Answer();
                questionAnswer.setQuestionContent(question.getContent());
                questionAnswer.setAnswers(answers);
                return questionAnswer;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all-lessons")
    public ResponseEntity<List<CreateQuestionForm>> getAllQuestionsAndAnswers(@RequestHeader("Authorization") String token) {
        try {
            token = token.replace("Bearer ", "");
            String username = jwtService.extractUsername(token);
            User mUser = userService.getUserByUsername(username);
            List<Lession> lessons = new ArrayList<>();

            if (mUser.getRoles().stream().map(Role::getName).anyMatch(roleName -> roleName == RoleName.ADMIN)) {
                lessons.addAll(lessionService.getAllLessions());
            } else {
                lessons.addAll(lessionService.getAllLessionsByUser(mUser));
            }

            List<CreateQuestionForm> response = lessons.stream().map(lesson -> {
                List<Question> questions = questionService.getQuestionsByLessonId(lesson.getId());
                List<Question_Answer> questionAnswers = questions.stream().map(question -> {
                    List<Answer> answers = answerService.getAnswersByQuestionId(question.getId());
                    Question_Answer questionAnswer = new Question_Answer();
                    questionAnswer.setQuestionContent(question.getContent());
                    questionAnswer.setAnswers(answers);
                    return questionAnswer;
                }).collect(Collectors.toList());

                return new CreateQuestionForm(lesson, questionAnswers);
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
