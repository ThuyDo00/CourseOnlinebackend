package com.dan.controller;

import com.dan.service.Course_UserService;
import com.dan.service.Course_UserSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Transactional
public class AcceptController {
    @Autowired
    private Course_UserService course_userService;
    @Autowired
    private Course_UserSubService course_userSubService;

    @GetMapping("/success")
    public String registerCourse(@RequestParam String orderInfo) {
        Long courseId = Long.parseLong(orderInfo);
        course_userService.createCourse_UserBySub(course_userSubService.getCourse_UserSub(courseId));
        course_userSubService.deleteCourse_UserSub(courseId);
        return "ordersuccess";
    }
}
