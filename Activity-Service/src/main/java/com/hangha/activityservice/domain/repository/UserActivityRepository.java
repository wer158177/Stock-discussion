package com.hangha.activityservice.domain.repository;

import com.hangha.activityservice.domain.entity.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityRepository extends JpaRepository<UserActivityLog, Long> {
}
