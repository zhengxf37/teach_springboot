package org.tutorial.tutorial_platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tutorial.tutorial_platform.dto.MatchStudentDTO;
import org.tutorial.tutorial_platform.dto.MatchTeacherDTO;
import org.tutorial.tutorial_platform.service.MatchService;
import org.tutorial.tutorial_platform.vo.MatchStudentVO;
import org.tutorial.tutorial_platform.vo.MatchTeacherVO;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    /**
     * 学生查找合适的老师
     */
    @PostMapping("/teachers")
    public ResponseEntity<List<MatchTeacherVO>> findTeachers(@RequestBody MatchTeacherDTO dto) {
        return ResponseEntity.ok(matchService.findTeachers(dto));
    }

    /**
     * 教师查找合适的学生
     */
    @PostMapping("/students")
    public ResponseEntity<List<MatchStudentVO>> findStudents(@RequestBody MatchStudentDTO dto) {
        return ResponseEntity.ok(matchService.findStudents(dto));
    }
}
