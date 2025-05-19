package org.tutorial.tutorial_platform.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tutorial.tutorial_platform.dto.MatchStudentDTO;
import org.tutorial.tutorial_platform.dto.MatchTeacherDTO;
import org.tutorial.tutorial_platform.pojo.Student;
import org.tutorial.tutorial_platform.pojo.Teacher;
import org.tutorial.tutorial_platform.repository.StudentRepository;
import org.tutorial.tutorial_platform.repository.TeacherRepository;
import org.tutorial.tutorial_platform.service.MatchService;
import org.tutorial.tutorial_platform.vo.MatchStudentVO;
import org.tutorial.tutorial_platform.vo.MatchTeacherVO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchServiceImp implements MatchService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<MatchTeacherVO> findTeachers(MatchTeacherDTO dto) {
        return teacherRepository.findAllBySubjectAndTeachGrade(dto.getSubject(), dto.getGrade()).stream()
                .filter(t -> t.getScore().doubleValue() >= dto.getMinScore())
                .map(this::toMatchTeacherVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MatchStudentVO> findStudents(MatchStudentDTO dto) {
        return studentRepository.findAllBySubjectAndGrade(dto.getSubject(), dto.getGrade()).stream()
                .filter(s -> s.getScore().doubleValue() >= dto.getMinScore())
                .map(this::toMatchStudentVO)
                .collect(Collectors.toList());
    }

    private MatchTeacherVO toMatchTeacherVO(Teacher t) {
        MatchTeacherVO vo = new MatchTeacherVO();
        vo.setUserId(t.getUser().getUserId());
        vo.setUsername(t.getUser().getUsername());
        vo.setGender(t.getGender());
        vo.setEducation(t.getEducation());
        vo.setTeachGrade(t.getTeachGrade());
        vo.setSubject(t.getSubject());
        vo.setScore(t.getScore());
        vo.setExperience(t.getExperience());
        vo.setHobby(t.getHobby());
        return vo;
    }

    private MatchStudentVO toMatchStudentVO(Student s) {
        MatchStudentVO vo = new MatchStudentVO();
        vo.setUserId(s.getUser().getUserId());
        vo.setUsername(s.getUser().getUsername());
        vo.setGender(s.getGender());
        vo.setGrade(s.getGrade());
        vo.setSubject(s.getSubject());
        vo.setScore(s.getScore());
        vo.setHobby(s.getHobby());
        vo.setGoal(s.getGoal());
        return vo;
    }
}
