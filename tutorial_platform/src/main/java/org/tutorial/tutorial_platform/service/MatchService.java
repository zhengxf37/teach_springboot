package org.tutorial.tutorial_platform.service;

import org.tutorial.tutorial_platform.dto.MatchTeacherDTO;
import org.tutorial.tutorial_platform.dto.MatchStudentDTO;
import org.tutorial.tutorial_platform.vo.MatchTeacherVO;
import org.tutorial.tutorial_platform.vo.MatchStudentVO;

import java.util.List;

public interface MatchService {
    List<MatchTeacherVO> findTeachers(MatchTeacherDTO dto);
    List<MatchStudentVO> findStudents(MatchStudentDTO dto);
}
