package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tutorial.tutorial_platform.pojo.Student;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tutorial.tutorial_platform.pojo.Teacher;

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
    /**
     * 自定义查询接口，用于根据向量进行查询和排序
     * @param teacherVector 学生向量
     * @param subject  科目（可选）
     * @param teachGrade 年级（可选）
     * @param pageable 分页参数
     * @return 分页的教师列表
     */
    @Query(
            value =
                    "SELECT t.*, " +
                            "SUM(vt.value * sv.value) / " +
                            "(SQRT(SUM(vt.value^2)) * SQRT(SUM(sv.value^2))) AS similarity " +
                            //余弦相似度计算为小数，similarity
                            //展开老师/学生向量，展开向量中的元素，并计算相似度
                            "FROM student t " +
                            //CROSS JOIN JSON_TABLE	将 JSON 数组展开为多行
                            "CROSS JOIN ( " +
                            "    SELECT jt.value, ROW_NUMBER() OVER () AS pos " +
                            "    FROM JSON_TABLE(t.vector, '$[*]' COLUMNS (value DECIMAL(10,2) PATH '$')) AS jt) vt " +
                            "JOIN ( " +
                            "    SELECT jt.value, ROW_NUMBER() OVER () AS pos " +
                            "    FROM JSON_TABLE(CAST(:teacherVector AS JSON), '$[*]' COLUMNS (value DECIMAL(10,2) PATH '$')) AS jt) sv ON vt.pos = sv.pos " +
                            //筛选条件，暂时2个条件，科目，学生年级
                            "WHERE 1=1 " +
                            "AND (:subject IS NULL OR t.subject = :subject) " +
                            "AND (:teachGrade IS NULL OR t.subject = :teachGrade) " +
                            //聚合老师，CROSS JOIN 固定语法
                            "GROUP BY t.user_id " +
                            //相似度降序排序
                            "ORDER BY similarity DESC",
            countQuery =
                    "SELECT COUNT( t.user_id) FROM student t " +
                            "WHERE 1=1 " +
                            "AND (:subject IS NULL OR t.subject = :subject) " +
                            "AND (:teachGrade IS NULL OR t.subject = :teachGrade)",
            nativeQuery = true
    )
    Page<Student> findAndSortByVector(
            @Param("teacherVector") List<Double> teacherVector, // 直接传递 List<Double>
            @Param("subject") String subject,
            @Param("teachGrade") String teachGrade,
            Pageable pageable
    );
}
