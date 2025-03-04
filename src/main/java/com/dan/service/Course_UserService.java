package com.dan.service;

import com.dan.model.Course;
import com.dan.model.Course_User;
import com.dan.model.Course_UserSub;
import com.dan.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface Course_UserService {
    Page<Course_User> getAllCourseByUser(User user, String name, Pageable pageable);
    Page<Course_User> getAllCourse_User(String keyword, Pageable pageable);
    public Course_User createCourse_User(Course_User course_user);
    public void createCourse_User(Course course, String username);
    public Course_User createCourse_UserBySub(Course_UserSub course_userSub);
    public Course_User updateCourse_User(Course_User course_user, Long id);
    public void deleteCourse_User(Long id);
    public List<Course_User> getCourse_UserByCourse(Course course);
}
