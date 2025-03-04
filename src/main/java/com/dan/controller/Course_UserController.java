package com.dan.controller;

import com.dan.model.Course;
import com.dan.model.Course_User;
import com.dan.model.Course_UserSub;
import com.dan.service.Course_UserService;
import com.dan.service.Course_UserSubService;
import com.dan.service.JwtService;
import com.dan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course_user")
public class Course_UserController {
    @Autowired
    private Course_UserService course_userService;
    @Autowired
    private Course_UserSubService course_userSubService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @PostMapping("/buy-course")
    public ResponseEntity<?> createCourse_User(@RequestHeader("Authorization") String token,
                                               @RequestParam(value = "course") Course course) {
        token = token.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);

        course_userSubService.createCourse_User(course, username);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/my-course-sub")
    public ResponseEntity<List<Course_UserSub>> getCourseUserSub(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);
        return new ResponseEntity<>(course_userSubService.getCourseUserSubByUsername(username), HttpStatus.CREATED);
    }

    @GetMapping("/course-sub")
    public ResponseEntity<List<Course_UserSub>> getCourseUsers() {
        return new ResponseEntity<>(course_userSubService.getCourseUserSubs(), HttpStatus.CREATED);
    }

    @DeleteMapping("/course-sub/{id}")
    public ResponseEntity<?> deleteCourseUserSub(@PathVariable Long id) {
        course_userSubService.deleteCourseUserSub(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @GetMapping("/my-courses")
    public ResponseEntity<Page<Course_User>> getAllCourseByUser(@RequestHeader("Authorization") String token,
                                                                @RequestParam(value = "name", defaultValue = "") String name,
                                                                @RequestParam(value = "page", defaultValue = "0") int page,
                                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                                @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                                                @RequestParam(value = "order", defaultValue = "desc") String order) {
        token = token.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortBy)));
        return new ResponseEntity<>(course_userService.getAllCourseByUser(userService.getUserByUsername(username), name, pageable), HttpStatus.OK);
    }
}
