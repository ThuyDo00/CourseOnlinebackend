package com.dan.repository;

import com.dan.model.Course_UserSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Course_UserSubRepository extends JpaRepository<Course_UserSub, Long> {
    Optional<Course_UserSub> findByCourseIdAndUserId(Long courseId, Long userId);

    List<Course_UserSub> findAllByUserId(Long userId);
}
