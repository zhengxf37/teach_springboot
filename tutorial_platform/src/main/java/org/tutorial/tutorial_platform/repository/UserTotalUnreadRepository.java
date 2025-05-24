package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tutorial.tutorial_platform.pojo.UserTotalUnread;

import java.util.Optional;

/**
 * UserTotalUnreadRepository - 用户未读消息汇总数据访问接口
 * 提供对 user_unread_summary 表的 CRUD 操作
 *
 * 元信息：
 * @author zxf
 */
public interface UserTotalUnreadRepository extends JpaRepository<UserTotalUnread, Long> {
    Optional<UserTotalUnread> findByUserId(Long userId);

    @Modifying  // 修改数据库
    @Query("UPDATE UserTotalUnread utu SET utu.totalUnread = utu.totalUnread + 1 WHERE utu.userId = :userId")
    void incrementTotalUnread(@Param("userId") Long userId);

    /**
     * 功能：指定用户减少count条未读消息
     * @param userId
     * @param count
     */
    @Modifying  // 修改数据库
    @Query("UPDATE UserTotalUnread utu SET utu.totalUnread = utu.totalUnread - :count WHERE utu.userId = :userId")
    void decrementTotalUnread(@Param("userId") Long userId, @Param("count") Integer count);
}