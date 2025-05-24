package org.tutorial.tutorial_platform.repository;

import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.tutorial.tutorial_platform.pojo.ChatMessage;

/**
 * ChatMessageRepository - 聊天消息数据访问接口
 * 提供对 chat_message 表的 CRUD 操作
 *
 * 元信息：
 * @author zxf
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findBySessionIdOrderByCreateTimeDesc(Long sessionId, Pageable pageable);

    // 根据SessionId删除所有与该会话有关的消息
    @Modifying
    @Transactional
    @Query("DELETE FROM ChatMessage cm WHERE cm.sessionId = :sessionId")
    void deleteMessageBySessionId(@Param("sessionId") Long sessionId);
}