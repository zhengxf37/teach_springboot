package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorial.tutorial_platform.pojo.UserComment;


public interface UserCommentRepository extends JpaRepository<UserComment, Long> {
    UserComment findByUserId(Long userId);
}
