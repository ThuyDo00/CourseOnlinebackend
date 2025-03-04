package com.dan.service;

import com.dan.model.Course;
import com.dan.model.Course_UserSub;

import java.util.List;

public interface Course_UserSubService {
    public Course_UserSub createCourse_UserSub(Course_UserSub course_UserSub);

    public void createCourse_User(Course course, String username);

    public Course_UserSub getCourse_UserSub(Long id);

    public void deleteCourse_UserSub(Long id);

    Course_UserSub getCourseUserSubByCourseIdAndUsername(Long courseId, String username);

    List<Course_UserSub> getCourseUserSubByUsername(String username);

    List<Course_UserSub> getCourseUserSubs();

    void deleteCourseUserSub(Long id);
}
