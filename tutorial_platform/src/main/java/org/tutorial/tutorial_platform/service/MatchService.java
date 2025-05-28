package org.tutorial.tutorial_platform.service;
import org.springframework.data.domain.Page;
import org.tutorial.tutorial_platform.dto.MatchTeacherDTO;
import org.tutorial.tutorial_platform.dto.MatchStudentDTO;
import org.tutorial.tutorial_platform.vo.MatchTeacherVO;
import org.tutorial.tutorial_platform.vo.MatchStudentVO;

import java.util.List;

public interface MatchService {
    Page<MatchTeacherVO> findTeachersWithAiRanking(MatchTeacherDTO dto);
    Page<MatchStudentVO> findStudentsWithAiRanking(MatchStudentDTO dto);


}
