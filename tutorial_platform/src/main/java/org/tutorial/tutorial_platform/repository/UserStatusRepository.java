package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorial.tutorial_platform.pojo.UserStatus;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {

}
