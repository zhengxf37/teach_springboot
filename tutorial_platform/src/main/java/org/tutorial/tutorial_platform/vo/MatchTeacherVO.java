package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.Education;
import org.tutorial.tutorial_platform.pojo.Gender;
import org.tutorial.tutorial_platform.pojo.TeachGrade;

import java.math.BigDecimal;

@Data
public class MatchTeacherVO {
    private Long userId;
    private String username;
    private Gender gender;
    private Education education;
    private TeachGrade teachGrade;
    private String subject;
    private BigDecimal score;
    private Integer experience;
    private String hobby;
}
