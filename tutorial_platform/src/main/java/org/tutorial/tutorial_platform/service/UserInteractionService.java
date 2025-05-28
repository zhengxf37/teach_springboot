package org.tutorial.tutorial_platform.service;

import org.tutorial.tutorial_platform.dto.JudgeUserDTO;
import org.tutorial.tutorial_platform.vo.UserCommentVO;

import java.util.List;

public interface UserInteractionService {


    /**
     * 发布需求
     * @return
     */
    Boolean publish(Long userId);

    /**
     * 删除需求
     * @return
     */
    Boolean delete(Long userId);

    /**
     * 查询需求
     *
     * @return
     */
    Integer query(Long userId);
    /**
     *  申请匹配
     * @return
     */
    Boolean want(Long userId, Long wantId);

    /**
     *  拒绝匹配
     * @return
     */
    Boolean reject(Long userId);

    /**
     *  增加评价
     * @return
     */
    Boolean judge(JudgeUserDTO judgeUserDTO);


    /**
     *  查询评价
     * @return
     */
    List<UserCommentVO> queryJudge(Long userId);
}
