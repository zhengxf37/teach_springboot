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
import org.tutorial.tutorial_platform.service.AiService;
import org.tutorial.tutorial_platform.vo.MatchStudentVO;
import org.tutorial.tutorial_platform.vo.MatchTeacherVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchServiceImp implements MatchService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AiService aiService;

    /**
     * 根据学生需求分页查询老师，过滤最低评分，调用AI排序，返回分页VO列表
     */
    @Override
    public Page<MatchTeacherVO> findTeachersWithAiRanking(MatchTeacherDTO dto) {
        // 创建分页请求对象
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize());

        // 从数据库分页查询符合科目和教授年级的老师（需确认TeacherRepository有对应方法）
        Page<Teacher> pageFromDb = teacherRepository.findAllBySubjectAndTeachGrade(dto.getSubject(), dto.getGrade(), pageRequest);

        // 过滤老师评分，防止null评分导致异常
        List<Teacher> filtered = pageFromDb.stream()
                .filter(t -> t.getScore() != null && t.getScore().doubleValue() >= dto.getMinScore())
                .collect(Collectors.toList());

        // 若过滤后列表为空，避免调用AI接口，直接返回空结果
        if (filtered.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }

        // 调用AI服务排序，传入过滤后的老师列表和匹配请求DTO，得到老师ID排序列表
        List<Long> sortedIds = aiService.rankTeachersByAi(filtered, dto);

        // 根据AI返回的ID顺序，重排老师列表
        List<Teacher> sorted = sortTeachersByIds(filtered, sortedIds);

        // 转换为前端VO对象列表
        List<MatchTeacherVO> vos = sorted.stream()
                .map(this::toMatchTeacherVO)
                .collect(Collectors.toList());

        // 返回分页结果，保持总记录数为数据库查询的总数
        return new PageImpl<>(vos, pageRequest, pageFromDb.getTotalElements());
    }

    /**
     * 根据教师需求分页查询学生，过滤最低评分，调用AI排序，返回分页VO列表
     */
    @Override
    public Page<MatchStudentVO> findStudentsWithAiRanking(MatchStudentDTO dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize());

        // 分页查询符合科目和年级的学生（需确认StudentRepository有对应方法）
        Page<Student> pageFromDb = studentRepository.findAllBySubjectAndGrade(dto.getSubject(), dto.getGrade(), pageRequest);

        // 过滤学生评分，防止null
        List<Student> filtered = pageFromDb.stream()
                .filter(s -> s.getScore() != null && s.getScore().doubleValue() >= dto.getMinScore())
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }

        // 调用AI排序接口，获取排序后的学生ID列表
        List<Long> sortedIds = aiService.rankStudentsByAi(filtered, dto);

        // 根据排序ID重排学生列表
        List<Student> sorted = sortStudentsByIds(filtered, sortedIds);

        // 转成VO列表
        List<MatchStudentVO> vos = sorted.stream()
                .map(this::toMatchStudentVO)
                .collect(Collectors.toList());

        return new PageImpl<>(vos, pageRequest, pageFromDb.getTotalElements());
    }

    /**
     * 根据ID列表重排老师顺序
     */
    private List<Teacher> sortTeachersByIds(List<Teacher> teachers, List<Long> sortedIds) {
        Map<Long, Teacher> map = teachers.stream()
                .collect(Collectors.toMap(Teacher::getTeacherId, t -> t));
        List<Teacher> sorted = new ArrayList<>();
        for (Long id : sortedIds) {
            Teacher t = map.get(id);
            if (t != null) {
                sorted.add(t);
            }
        }
        return sorted;
    }

    /**
     * 根据ID列表重排学生顺序
     */
    private List<Student> sortStudentsByIds(List<Student> students, List<Long> sortedIds) {
        Map<Long, Student> map = students.stream()
                .collect(Collectors.toMap(Student::getStudentId, s -> s));
        List<Student> sorted = new ArrayList<>();
        for (Long id : sortedIds) {
            Student s = map.get(id);
            if (s != null) {
                sorted.add(s);
            }
        }
        return sorted;
    }

    /**
     * 将Teacher实体转换为VO供前端使用
     */
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

    /**
     * 将Student实体转换为VO供前端使用
     */
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

    /**
     * 构建AI提示词（示例，如有需要可调用）
     */
    private String buildAiPrompt(MatchTeacherDTO dto, List<Teacher> teachers) {
        StringBuilder sb = new StringBuilder();
        sb.append("请根据以下学生需求和老师信息，按照匹配程度从高到低排序老师，并返回老师ID列表，逗号分隔。\n");
        sb.append("学生需求：科目=").append(dto.getSubject()).append(", 年级=").append(dto.getGrade())
                .append(", 最低评分=").append(dto.getMinScore()).append("\n");
        sb.append("老师列表：\n");
        for (Teacher t : teachers) {
            sb.append("ID: ").append(t.getTeacherId())
                    .append(", 评分: ").append(t.getScore())
                    .append(", 教育: ").append(t.getEducation())
                    .append(", 教授年级: ").append(t.getTeachGrade())
                    .append(", 经验: ").append(t.getExperience())
                    .append(", 爱好: ").append(t.getHobby())
                    .append("\n");
        }
        sb.append("请只返回ID列表，格式如：123,456,789");
        return sb.toString();
    }

    /**
     * 解析AI返回的字符串，转换为ID列表
     */
    private List<Long> parseAiRanking(String aiResponse) {
        return Arrays.stream(aiResponse.trim().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}
