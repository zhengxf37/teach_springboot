package org.tutorial.tutorial_platform.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tutorial.tutorial_platform.pojo.ChatSession;
import org.tutorial.tutorial_platform.pojo.UserSessionMapping;

import java.util.Optional;

/**
 * ChatSessionRepository - 聊天会话数据访问接口
 * 提供对 chat_session 表的 CRUD 操作
 * 元信息：
 * @author zxf
 */
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    // 查询逻辑：满足下列两种情况之一即可：
    // 1. teacherUserId 等于 userId1 且 studentUserId 等于 userId2
    // 2. teacherUserId 等于 userId2 且 studentUserId 等于 userId1
    // 功能：用于在创建会话时查询是否已经存在会话，返回会话或者
    @Query("SELECT cs FROM ChatSession cs " +
            "WHERE (cs.teacherUserId = :userId1 AND cs.studentUserId = :userId2) " +
            "OR (cs.teacherUserId = :userId2 AND cs.studentUserId = :userId1)")
    Optional<ChatSession> findByUserIds(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2
    );  // @Param用来将方法参数映射到JPQL中的命名参数:userId上

    // 通过sessionId和studentUserId查找
    @Query("SELECT cs FROM ChatSession cs " +
            "WHERE cs.sessionId = :sessionId " +
            "AND cs.studentUserId = :studentUserId ")
    Optional<ChatSession> findBySessionIdAndStudentUserId(
            @Param("sessionId") Long sessionId,
            @Param("studentUserId") Long studentUserId
    );

    // 通过sessionId和teacherUserId查找
    @Query("SELECT cs FROM ChatSession cs " +
            "WHERE cs.sessionId = :sessionId " +
            "AND cs.teacherUserId = :teacherUserId ")
    Optional<ChatSession> findBySessionIdAndTeacherUserId(
            @Param("sessionId") Long sessionId,
            @Param("teacherUserId") Long teacherUserId
    );

    // 根据sessionId删除会话
    @Modifying
    @Transactional
    @Query("DELETE FROM ChatSession cs WHERE cs.sessionId = :sessionId")
    void deleteSessionBySessionId(@Param("sessionId") Long sessionId);
}