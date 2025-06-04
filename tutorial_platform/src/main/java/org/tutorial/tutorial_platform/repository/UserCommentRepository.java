package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorial.tutorial_platform.pojo.UserComment;

import java.util.List;


public interface UserCommentRepository extends JpaRepository<UserComment, Long> {
    List<UserComment> findByUserId(Long userId);

    List<UserComment> findByUserIdAndFromId(Long userId, Long fromId);
}
