package org.tutorial.tutorial_platform.controller;

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

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;

    // ✅ 使用构造函数注入
    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // ✅ 新增路径：保存带向量的学生
    @PostMapping("/student/vector")
    public ResponseEntity<Student> saveStudentWithVector(@RequestBody Student student) {
        Student saved = matchService.saveStudentWithVector(student);
        return ResponseEntity.ok(saved);
    }

    // ✅ 新增路径：保存带向量的老师
    @PostMapping("/teacher/vector")
    public ResponseEntity<Teacher> saveTeacherWithVector(@RequestBody Teacher teacher) {
        Teacher saved = matchService.saveTeacherWithVector(teacher);
        return ResponseEntity.ok(saved);
    }

    // ✅ 获取 AI 排序后的教师列表
    @PostMapping("/teachers/ai")
    public ResponseEntity<Page<MatchTeacherVO>> findTeachersWithAi(@RequestBody MatchStudentDTO dto) {
        return ResponseEntity.ok(matchService.findTeachersWithAiRanking(dto));
    }

    // ✅ 获取 AI 排序后的学生列表
    @PostMapping("/students/ai")
    public ResponseEntity<Page<MatchStudentVO>> findStudentsWithAi(@RequestBody MatchTeacherDTO dto) {
        return ResponseEntity.ok(matchService.findStudentsWithAiRanking(dto));
    }
}
