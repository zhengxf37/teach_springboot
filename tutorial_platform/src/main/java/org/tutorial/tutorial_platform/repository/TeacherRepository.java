package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorial.tutorial_platform.pojo.Teacher;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 * 教师数据访问接口
 * 提供对 teacher 表的CRUD操作
 */
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    /**
     * 根据用户ID查找教师信息
     * @param userId 用户ID
     * @return 教师实体
     */
    Optional<Teacher> findByUserUserId(Long userId);
    //TODO老师数据库

    /**
     * 查询用户Id是否在老师表中
     */
    boolean existsByUserUserId(Long userId);



    Page<Teacher> findAllBySubjectAndTeachGrade(String subject, String teachGrade, Pageable pageable);
}
