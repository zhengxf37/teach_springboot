package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorial.tutorial_platform.pojo.TeachGrade;
import org.tutorial.tutorial_platform.pojo.Teacher;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 教师数据访问接口
 * 提供对 teacher 表的增删改查操作
 */
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * 根据用户 ID 查找教师信息
     *
     * @param userId 用户 ID
     * @return 封装在 Optional 中的教师实体
     */
    Optional<Teacher> findByUserUserId(Long userId);

    /**
     * 判断某个用户 ID 是否已存在于教师表中
     *
     * @param userId 用户 ID
     * @return true 表示存在
     */
    boolean existsByUserUserId(Long userId);


    /**
     * 根据科目和教授年级分页查询教师列表（不考虑性别）
     *
     * @param subject 科目
     * @param teachGrade 教授年级
     * @param pageable 分页参数
     * @return 分页的教师列表
     */
    // 假设 TeachGrade 是一个枚举类型
    Page<Teacher> findAllBySubjectAndTeachGrade(String subject, TeachGrade teachGrade, Pageable pageable);

}
