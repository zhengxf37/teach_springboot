package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorial.tutorial_platform.pojo.Student;
import java.util.List;
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
    /**
     * 根据科目和年级查询学生列表
     * @param subject 科目名称
     * @param grade 年级
     * @return 学生列表
     */
    List<Student> findAllBySubjectAndGrade(String subject, String grade);

    /**
     * 查询用户Id是否在学生表中
     */
    boolean existsByUserUserId(Long userId);
}
