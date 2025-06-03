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
                            "    SUM(tvt.tvt_value * svt.svt_value) / " +
                            "    (SQRT(SUM(tvt.tvt_value * tvt.tvt_value)) * SQRT(SUM(svt.svt_value * svt.svt_value))) AS similarity " +
                            "FROM ( " +
                            "    SELECT * " +
                            "    FROM student " +//1
                            "    WHERE 1=1 " +
//                            "        AND (:subject IS NULL OR subject = :subject) " +
//                            "        AND (:teachGrade IS NULL OR teach_grade = :teachGrade) " +
                            ") t " +
                            "JOIN (" +
                            "    SELECT " +
                            "        sub.user_id, " +
                            "        sub.`value` AS svt_value, " +//2
                            "        ROW_NUMBER() OVER (PARTITION BY sub.user_id ORDER BY (SELECT NULL)) AS pos " +
                            "    FROM (" +
                            "        SELECT user_id, jt.`value` " +
                            "        FROM student " + //3
                            "        CROSS JOIN JSON_TABLE( " +
                            "            vector, " +
                            "            '$[*]' COLUMNS ( " +
                            "                `value` DECIMAL(10,2) PATH '$' " +
                            "            ) " +
                            "        ) AS jt " +
                            "    ) AS sub " +
                            ") svt ON t.user_id = svt.user_id " +
                            "JOIN (" +
                            "    SELECT " +
                            "        `value` AS tvt_value, " +
                            "        ROW_NUMBER() OVER () AS pos " +
                            "    FROM JSON_TABLE( " +
                            "        CAST('[1.0, 0.8, 1.6]' AS JSON), " +
                            "        '$[*]' COLUMNS ( " +
                            "            `value` DECIMAL(10,2) PATH '$' " +
                            "        ) " +
                            "    ) AS jt " +
                            ") tvt ON svt.pos = tvt.pos " +
                            "GROUP BY t.user_id " +
                            "ORDER BY similarity DESC",
            countQuery =
                    "SELECT COUNT(*) FROM student t " +
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
