package org.tutorial.tutorial_platform.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tutorial.tutorial_platform.dto.JudgeUserDTO;
import org.tutorial.tutorial_platform.pojo.User;
import org.tutorial.tutorial_platform.pojo.UserComment;
import org.tutorial.tutorial_platform.pojo.UserStatus;
import org.tutorial.tutorial_platform.repository.UserCommentRepository;
import org.tutorial.tutorial_platform.repository.UserRepository;
import org.tutorial.tutorial_platform.repository.UserStatusRepository;
import org.tutorial.tutorial_platform.service.UserInteractionService;
import org.tutorial.tutorial_platform.vo.UserCommentVO;

import java.util.List;

/**
 * 用户交互服务实现类
 * 状态：：0-未公开，1-公开中，2-匹配中，3-被匹配中，4-拒绝，5-被拒绝，6-完成，7-申请方取消
 */
@Slf4j
@Service
public class UserInteractionServiceImp implements UserInteractionService {
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private UserCommentRepository userCommentRepository;
    @Autowired
    private UserRepository userRepository;
    /**
     * 发布需求
     * @param userId 用户ID
     * @return 更新结果
     */
//    public Boolean publish(Long userId) {
//        // 1. 查询用户状态实体并报错
//        UserStatus userStatus = userStatusRepository.findById(userId)
//                .orElseGet(() -> {
//                    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//                    UserStatus userStatus1 = new UserStatus();
//                    userStatus1.setUser(user);
//                    return userStatus1;
//                });
//        // 2. 更新用户信息
//        userStatus.setStatus(1);
//        userStatus.setWantId(0L);
//
//
//        // 3. 保存更新
//        userStatusRepository.save(userStatus);
//        return true;
//
//    }
//    /**
//     * 删除需求
//     * @param userId 用户ID
//     * @return 更新结果
//     */
//    public Boolean delete(Long userId){
//        // 1. 查询用户状态实体并报错
//        UserStatus userStatus = userStatusRepository.findById(userId)
//                .orElseGet(() -> {
//                    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//                    UserStatus userStatus1 = new UserStatus();
//                    userStatus1.setUser(user);
//                    return userStatus1;
//                });
//        // 2. 更新用户信息
//        userStatus.setStatus(0);
//        userStatus.setWantId(0L);
//
//        // 3. 保存更新
//        userStatusRepository.save(userStatus);
//        return true;
//    }
//    /**
//     * 查询需求
//     *
//     * @param userId 用户ID
//     * @return 状态码
//     */
//    public Integer query(Long userId){
//        // 1. 查询用户状态实体并报错
//        UserStatus userStatus = userStatusRepository.findById(userId)
//                .orElseGet(() -> {
//                    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//                    UserStatus userStatus1 = new UserStatus();
//                    userStatus1.setUser(user);
//                    userStatus1.setStatus(0);
//                    userStatus1.setWantId(0L);
//                    return userStatus1;
//                });
//        return userStatus.getStatus();
//    }
//    /**
//     *  申请匹配
//     *  @param userId 用户ID
//     * @return 更新结果
//     */
//    public Boolean want(Long userId, Long wantId){
//        //没有判断老师/学生，直接设置匹配对象
//        UserStatus userStatus = userStatusRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("申请方用户状态信息不存在"));
//        UserStatus userStatus2 = userStatusRepository.findById(wantId)
//                .orElseThrow(() -> new RuntimeException("被申请用户状态信息不存在"));
//        //申请匹配，状态码为2，被申请码为3
//        userStatus.setStatus(2);
//        userStatus.setWantId(wantId);
//        userStatus2.setStatus(3);
//        userStatus2.setWantId(userId);
//        userStatusRepository.save(userStatus2);
//        userStatusRepository.save(userStatus);
//        return true;
//    }
//    /**
//     *  拒绝匹配
//     *  @param userId 用户ID
//     * @return 更新结果
//     */
//    public Boolean reject(Long userId){
//        UserStatus userStatus = userStatusRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("被申请用户状态信息不存在，请先公开用户来触发程序自行创建数据表"));
//        UserStatus userStatus2 = userStatusRepository.findById(userStatus.getWantId())
//                .orElseThrow(() -> new RuntimeException("申请方用户状态信息不存在，请先公开用户来触发程序自行创建数据表"));
//        if (userStatus.getStatus() == 3) {
//
//            userStatus.setStatus(4);
//            userStatus2.setStatus(5);
//        }else{
//            userStatus.setStatus(7);
//            userStatus2.setStatus(7);
//        }
//        userStatusRepository.save(userStatus2);
//        userStatusRepository.save(userStatus);
//
//        return true;
//    }
//    /**
//     *  同意匹配
//     * @param userId
//     * @return
//     */
//    @Override
//    public Boolean agree(Long userId) {
//        UserStatus userStatus = userStatusRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("被申请用户状态信息不存在，请先公开用户来触发程序自行创建数据表"));
//        UserStatus userStatus2 = userStatusRepository.findById(userStatus.getWantId())
//                .orElseThrow(() -> new RuntimeException("申请方用户状态信息不存在，请先公开用户来触发程序自行创建数据表"));
//        if (userStatus.getStatus() == 3) {
//            userStatus.setStatus(6);
//            userStatus2.setStatus(6);
//        }else {
//            log.info("用户状态码错误,不是被匹配状态");
//            return false;
//        }
//
//
//        return null;
//    }

    /**
     *  增加评价
     *  @param judgeUserDTO 评价信息
     * @return 更新结果
     */
    public Boolean judge(JudgeUserDTO judgeUserDTO){
        Long userId = judgeUserDTO.getUserId();
        Long judgeId = judgeUserDTO.getJudgeId();
        String ans = judgeUserDTO.getContent();
        List<UserComment> existingComment = userCommentRepository.findByUserIdAndFromId( judgeId,userId);
        if (existingComment.size() > 0) {
            UserComment comment = existingComment.get(0);
            comment.setContent(ans);
            userCommentRepository.save(comment);
        } else {
            UserComment comment = new UserComment(judgeId,userId, ans);
            userCommentRepository.save(comment);
        }

        log.info("评价成功,评价id={}",userId);


        return true;
    }

    /**
     *  查询评价
     * @return
     */
    public List<UserCommentVO> queryJudge(Long userId){
        List<UserComment> entityList = userCommentRepository.findByUserId(userId);
        List<UserCommentVO> vos = entityList.stream()
                .map(comment -> new UserCommentVO(
                        comment.getFromId(),
                        comment.getContent()
                ))
                .toList();
        log.info("查询评价成功,用户id={}",userId);
        return vos;
    }

}
