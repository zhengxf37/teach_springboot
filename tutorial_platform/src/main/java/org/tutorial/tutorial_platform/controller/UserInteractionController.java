package org.tutorial.tutorial_platform.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tutorial.tutorial_platform.dto.JudgeUserDTO;
import org.tutorial.tutorial_platform.service.UserInteractionService;
import org.tutorial.tutorial_platform.vo.UserCommentVO;

import java.util.List;

/**
 * UserInteractionController-用户状态管理类
 * 功能：设置用户匹配进程的状态，添加评价
 *
 * 用户状态包括：
 * 用户id
 * 状态：：0-未公开，1-公开中，2-匹配中，3-被匹配中，4-拒绝，5-被拒绝，6-完成，7-申请方取消
 * 匹配对象id
 *
 * 用户评价：单独的函数和数据库
 * zhj
 */
@RestController
@RequestMapping("/api/interaction")

public class UserInteractionController {
    @Autowired
    private UserInteractionService userInteractionService;

    /**
     * 公开信息用于匹配
     * @param request
     * @return
     */
    @GetMapping("/publish")
    public ResponseEntity<Boolean> publish(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userInteractionService.publish(userId));

    }
    /**
     * 删除需求
     * @param request
     * @return
     */
    @GetMapping("/delete")
    public ResponseEntity<Boolean> delete(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userInteractionService.delete(userId));


    }
    /**
     * 查询需求
     *
     * @param request
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<Integer> query(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userInteractionService.query(userId));
    }

    /**
     * 请求进行匹配
     * @param request
     * @return
     */
    @GetMapping("/want")
    public ResponseEntity<Boolean> want(HttpServletRequest request, @RequestParam Long wantId){
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userInteractionService.want(userId, wantId));


    }
    /**
     * 拒绝匹配
     * @param request
     * @return
     */
    @GetMapping("/reject")
    public ResponseEntity<Boolean> reject(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userInteractionService.reject(userId));

    }
    /**
     * 评价
     * @param request
     * @return
     */
    @PostMapping("/judge")
    public ResponseEntity<Boolean> judge(HttpServletRequest request, @RequestBody JudgeUserDTO judgeUserDTO){
        Long userId = (Long) request.getAttribute("userId");
        judgeUserDTO.setJudgeId(userId);
        return ResponseEntity.ok(userInteractionService.judge(judgeUserDTO));

    }
    /**
     * 查询评价
     * @param request 获取用户id
     * @return 评价信息
     */
    @GetMapping("/queryjudge")
    public ResponseEntity<List<UserCommentVO>> queryJudge(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userInteractionService.queryJudge(userId));

    }
}
