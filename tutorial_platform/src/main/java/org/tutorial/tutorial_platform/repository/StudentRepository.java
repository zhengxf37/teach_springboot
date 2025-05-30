package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorial.tutorial_platform.pojo.Student;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 学生数据访问接口
 * 提供对学生表的增删改查操作
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * 根据用户 ID 查询学生信息
     *
     * @param userId 用户 ID
     * @return 封装在 Optional 中的学生对象
     */
    Optional<Student> findByUserUserId(Long userId);

    /**
     * 根据科目和年级分页查询学生列表（不考虑性别）
     *
     * @param subject 科目
     * @param grade 年级
     * @param pageable 分页参数
     * @return 分页的学生列表
     */
    Page<Student> findAllBySubjectAndGrade(String subject, String grade, Pageable pageable);
}
