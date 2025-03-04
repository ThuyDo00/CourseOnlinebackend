package com.dan.service.impl;

import com.dan.model.Course;
import com.dan.model.Course_UserSub;
import com.dan.model.User;
import com.dan.repository.Course_UserSubRepository;
import com.dan.service.Course_UserSubService;
import com.dan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class Course_UserSubServiceImpl implements Course_UserSubService {
    @Autowired
    private Course_UserSubRepository course_UserSubRepository;
    @Autowired
    private UserService userService;

    @Override
    public Course_UserSub createCourse_UserSub(Course_UserSub course_UserSub) {
        return course_UserSubRepository.save(course_UserSub);
    }

    @Override
    @Transactional
    public void createCourse_User(Course course, String username) {
        User user = userService.getUserByUsername(username);
        Course_UserSub course_user = new Course_UserSub();
        course_user.setCourse(course);
        course_user.setUser(user);
        course_user.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        course_UserSubRepository.save(course_user);
    }

    @Override
    public Course_UserSub getCourse_UserSub(Long id) {
        return course_UserSubRepository.findById(id).orElseThrow(() -> new RuntimeException("Course_UserSub not found"));
    }

    @Override
    @Transactional
    public void deleteCourse_UserSub(Long id) {
        course_UserSubRepository.deleteById(id);
    }

    @Override
    public Course_UserSub getCourseUserSubByCourseIdAndUsername(Long courseId, String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }
        return course_UserSubRepository.findByCourseIdAndUserId(courseId, user.getId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    @Override
    public List<Course_UserSub> getCourseUserSubByUsername(String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }
        return course_UserSubRepository.findAllByUserId(user.getId());
    }

    @Override
    public List<Course_UserSub> getCourseUserSubs() {
        return course_UserSubRepository.findAll();
    }

    @Override
    public void deleteCourseUserSub(Long id) {
        if (!course_UserSubRepository.existsById(id)) {
            throw new RuntimeException("Course_UserSub not found with id: " + id);
        }
        course_UserSubRepository.deleteById(id);
    }
}
