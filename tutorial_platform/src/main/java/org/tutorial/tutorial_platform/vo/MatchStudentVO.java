package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.Gender;
import java.math.BigDecimal;

@Data
public class MatchStudentVO {
    private Long userId;
    private String username;
    private Gender gender;
    private String grade;
    private String subject;
    private BigDecimal score;
    private String hobby;
    private String goal;
}
