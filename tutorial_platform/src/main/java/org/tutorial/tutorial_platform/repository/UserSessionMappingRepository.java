package org.tutorial.tutorial_platform.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tutorial.tutorial_platform.pojo.UserSessionMapping;

import java.util.List;
import java.util.Optional;

/**
 * UserSessionMappingRepository - 用户会话映射数据访问接口
 * 提供对 user_session_mapping 表的 CRUD 操作
 *
 * 元信息：
 * @author zxf
 */
public interface UserSessionMappingRepository extends JpaRepository<UserSessionMapping, Long> {
    /**
     * 根据userId和sessionId查询是否存在对应的用户会话
     */
    @Query("SELECT usm FROM UserSessionMapping usm WHERE usm.userId = :userId AND usm.sessionId = :sessionId")
    Optional<UserSessionMapping> findByUserIdAndSessionId(@Param("userId") Long userId, @Param("sessionId") Long sessionId);

    /**
     * 根据用户ID获取所有会话，按照最后消息时间排序
     */
    @Query("SELECT usm FROM UserSessionMapping usm " +
            "JOIN usm.chatSession cs " +  // 使用关联对象导航
           "WHERE usm.userId = :userId " +  // 筛选条件
           "ORDER BY cs.lastMessageTime DESC NULLS LAST")  // 时间倒序排列，Null排最后
    Page<UserSessionMapping> findUserSessionsOrderByLastMessageTime(
            @Param("userId") Long userId,
            Pageable pageable
    );

    /**
     * 根据sessionId删除用户会话映射
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserSessionMapping usm WHERE usm.sessionId = :sessionId")
    void deleteMappingBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 根据sessionId查找所有用户会话映射（返回列表，可能为空）
     */
    @Query("SELECT usm FROM UserSessionMapping usm WHERE usm.sessionId = :sessionId")
    List<UserSessionMapping> findAllBySessionId(@Param("sessionId") Long sessionId);
}

