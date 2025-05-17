package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorial.tutorial_platform.pojo.Student;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserUserId(Long userId);


}
