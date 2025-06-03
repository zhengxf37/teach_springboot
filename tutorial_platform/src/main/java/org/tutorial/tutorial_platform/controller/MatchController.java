package org.tutorial.tutorial_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import org.tutorial.tutorial_platform.dto.MatchStudentDTO;
import org.tutorial.tutorial_platform.dto.MatchTeacherDTO;
import org.tutorial.tutorial_platform.pojo.Student;
import org.tutorial.tutorial_platform.pojo.Teacher;
import org.tutorial.tutorial_platform.service.MatchService;
import org.tutorial.tutorial_platform.vo.MatchStudentVO;
import org.tutorial.tutorial_platform.vo.MatchTeacherVO;
/**
 * MatchController - 匹配相关
 * 包括学生与教师的向量保存、基于 AI 的排序推荐功能。
 */
@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private MatchService matchService;



    /**
     * 保存带有向量的用户信息。
     *
     * @return 已保存的学生对象
     */
    @GetMapping("/save")
    public ResponseEntity<String> saveWithVector(HttpServletRequest request) throws JsonProcessingException {

        Long userId = (Long) request.getAttribute("userId");
        matchService.saveWithVector(userId);
        return ResponseEntity.ok("正在保存向量");
    }

    /**
     * 根据学生信息获取 AI 排序后的教师列表。
     *
     * @param matchStudentDTO 匹配学生数据传输对象
     * @return 分页的教师匹配结果视图对象
     */
    @PostMapping("/teacher/ai")
    public ResponseEntity<Page<MatchTeacherVO>> findTeachersWithAi(HttpServletRequest request,@RequestBody MatchStudentDTO matchStudentDTO) throws JsonProcessingException {
        Long userId = (Long) request.getAttribute("userId");
        matchStudentDTO.setStudentId(userId);
        return ResponseEntity.ok(matchService.findTeachersWithAi( matchStudentDTO));
    }

    /**
     * 根据教师信息获取 AI 排序后的学生列表。
     *
     * @param matchTeacherDTO 匹配教师数据传输对象
     * @return 分页的学生匹配结果视图对象
     */
    @PostMapping("/student/ai")
    public ResponseEntity<Page<MatchStudentVO>> findStudentsWithAi(HttpServletRequest request,@RequestBody MatchTeacherDTO matchTeacherDTO) throws JsonProcessingException {
        Long userId = (Long) request.getAttribute("userId");
        matchTeacherDTO.setTeacherId(userId);
        return ResponseEntity.ok(matchService.findStudentsWithAi(matchTeacherDTO));
    }
}
