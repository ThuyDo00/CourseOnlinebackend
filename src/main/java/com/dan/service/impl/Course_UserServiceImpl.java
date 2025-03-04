package com.dan.service.impl;

import com.dan.model.Course;
import com.dan.model.Course_User;
import com.dan.model.Course_UserSub;
import com.dan.model.User;
import com.dan.repository.Course_UserRepository;
import com.dan.service.Course_UserService;
import com.dan.service.Course_UserSubService;
import com.dan.service.UserService;
import com.dan.util.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class Course_UserServiceImpl implements Course_UserService {
    @Autowired
    private Course_UserRepository course_userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private Course_UserSubService course_userSubService;

    @Override
    public Page<Course_User> getAllCourseByUser(User user, String name, Pageable pageable) {
        return course_userRepository.findByUserAndNameContain(user, name, pageable);
    }

    @Override
    public Page<Course_User> getAllCourse_User(String keyword, Pageable pageable) {
        return course_userRepository.findByKeyword(keyword, pageable);
    }

    @Override
    @Transactional
    public Course_User createCourse_User(Course_User course_user) {
        return course_userRepository.save(course_user);
    }

    @Override
    @Transactional
    public void createCourse_User(Course course, String username) {
        User user = userService.getUserByUsername(username);
        LogUtils.init();
        Course_User course_user = new Course_User();
        course_user.setCourse(course);
        course_user.setUser(user);
        course_user.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        course_userRepository.save(course_user);
    }

    @Override
    @Transactional
    public Course_User createCourse_UserBySub(Course_UserSub course_userSub) {
        Course_User course_user = new Course_User();
        course_user.setCourse(course_userSub.getCourse());
        course_user.setUser(course_userSub.getUser());
        course_user.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        return course_userRepository.save(course_user);
    }

    @Override
    @Transactional
    public Course_User updateCourse_User(Course_User course_user, Long id) {
        return course_userRepository.findById(id).map(cu -> {
            cu.setCourse(course_user.getCourse());
            cu.setUser(course_user.getUser());
            cu.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            return course_userRepository.save(cu);
        }).orElseThrow(() -> new RuntimeException("Course_User not found"));
    }

    @Override
    @Transactional
    public void deleteCourse_User(Long id) {
        course_userRepository.deleteById(id);
    }

    @Override
    public List<Course_User> getCourse_UserByCourse(Course course) {
        return course_userRepository.findByCourse(course);
    }
}
