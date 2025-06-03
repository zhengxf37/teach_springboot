package org.tutorial.tutorial_platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.tutorial.tutorial_platform.dto.MatchStudentDTO;
import org.tutorial.tutorial_platform.dto.MatchTeacherDTO;
import org.tutorial.tutorial_platform.pojo.Student;
import org.tutorial.tutorial_platform.vo.MatchStudentVO;
import org.tutorial.tutorial_platform.vo.MatchTeacherVO;

/**
 * 匹配服务接口：提供学生与教师之间的智能匹配功能
 */
public interface MatchService {


    /**
     * 存储向量
     *
     * @param userId 前端传入的匹配参数，包含分页信息及筛选条件
     * @return 分页结果，包含按相似度排序的教师 VO 列表
     */
    void saveWithVector(Long userId) throws JsonProcessingException;
    /**
     * 根据学生匹配条件（如科目、年级）获取 AI 排序后的老师列表
     *
     * @param matchStudentDTO 前端传入的匹配参数，包含分页信息及筛选条件
     * @return 分页结果，包含按相似度排序的教师 VO 列表
     */

    Page<MatchTeacherVO> findTeachersWithAi(MatchStudentDTO matchStudentDTO) throws JsonProcessingException;

    /**
     * 根据教师的匹配条件（如科目、年级）获取 AI 排序后的学生列表
     *
     * @param matchTeacherDTO 前端传入的匹配参数，包含分页信息及筛选条件
     * @return 分页结果，包含按相似度排序的学生 VO 列表
     */
    Page<MatchStudentVO> findStudentsWithAi(MatchTeacherDTO matchTeacherDTO) throws JsonProcessingException;
}
