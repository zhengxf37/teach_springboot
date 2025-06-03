package org.tutorial.tutorial_platform.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import org.tutorial.tutorial_platform.pojo.Teacher;


import java.util.List;
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
     * @return 分页的教师列表
     */
    List<Teacher> findAll();
    /**
     * 自定义查询接口，用于根据向量进行查询和排序
     * @param studentVector 学生向量
     * @param subject  科目（可选）
     * @param teachGrade 年级（可选）
     * @param pageable 分页参数
     * @return 分页的教师列表
     */
    /*
    @Query(
            value =
                    "SELECT t.*, " +
                            "    SUM(tvt.tvt_value * svt.svt_value) / " +
                            "    (SQRT(SUM(tvt.tvt_value * tvt.tvt_value)) * SQRT(SUM(svt.svt_value * svt.svt_value))) AS similarity " +
                            "FROM ( " +
                            "    SELECT * " +
                            "    FROM teacher " +
                            "    WHERE 1=1 " +
                            "        AND (:subject IS NULL OR subject = :subject) " +
                            "        AND (:teachGrade IS NULL OR teach_grade = :teachGrade) " +
                            ") t " +
                            "JOIN (" +
                            "    SELECT " +
                            "        sub.user_id, " +
                            "        sub.`value` AS tvt_value, " +
                            "        ROW_NUMBER() OVER (PARTITION BY sub.user_id ORDER BY (SELECT NULL)) AS pos " +
                            "    FROM (" +
                            "        SELECT user_id, jt.`value` " +
                            "        FROM teacher " +
                            "        CROSS JOIN JSON_TABLE( " +
                            "            vector, " +
                            "            '$[*]' COLUMNS ( " +
                            "                `value` DECIMAL(10,2) PATH '$' " +
                            "            ) " +
                            "        ) AS jt " +
                            "    ) AS sub " +
                            ") tvt ON t.user_id = tvt.user_id " +
                            "JOIN (" +
                            "    SELECT " +
                            "        `value` AS svt_value, " +
                            "        ROW_NUMBER() OVER () AS pos " +
                            "    FROM JSON_TABLE( " +
                            "        :studentVector, " +
                            "        '$[*]' COLUMNS ( " +
                            "            `value` DECIMAL(10,2) PATH '$' " +
                            "        ) " +
                            "    ) AS jt " +
                            ") svt ON tvt.pos = svt.pos " +
                            "GROUP BY t.user_id " +
                            "ORDER BY similarity DESC",
            countQuery =
                    "SELECT COUNT(*) FROM teacher t " +
                            "WHERE 1=1 " +
                            "AND (:subject IS NULL OR t.subject = :subject) " +
                            "AND (:teachGrade IS NULL OR t.teach_grade = :teachGrade)",
            nativeQuery = true
    )*/



//    Page<Teacher> findAndSortByVector(
//            @Param("studentVector") String studentVector, // 直接传递 List<Double>
//            @Param("subject") String subject,
//            @Param("teachGrade") String teachGrade,
//            Pageable pageable
//    );

}


