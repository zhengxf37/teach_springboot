package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorial.tutorial_platform.pojo.Student;

import java.util.Optional;

/**
 * 学生数据访问接口
 * @author zhj
 */
public interface StudentRepository extends JpaRepository<Student, Long> {
    /**
     * 根据用户id查询学生信息
     * @param userId 用户id
     * @return 学生信息
     */
    Optional<Student> findByUserUserId(Long userId);


}
