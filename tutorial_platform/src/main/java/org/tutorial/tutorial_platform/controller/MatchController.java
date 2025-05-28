package org.tutorial.tutorial_platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

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

    @PostMapping("/teachers/ai")
    public ResponseEntity<Page<MatchTeacherVO>> findTeachersWithAi(@RequestBody MatchTeacherDTO dto) {
        return ResponseEntity.ok(matchService.findTeachersWithAiRanking(dto));
    }

    @PostMapping("/students/ai")
    public ResponseEntity<Page<MatchStudentVO>> findStudentsWithAi(@RequestBody MatchStudentDTO dto) {
        return ResponseEntity.ok(matchService.findStudentsWithAiRanking(dto));
    }

}
