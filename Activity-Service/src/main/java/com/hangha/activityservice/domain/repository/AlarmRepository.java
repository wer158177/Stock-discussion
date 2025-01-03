package com.hangha.activityservice.domain.repository;

import com.hangha.activityservice.domain.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByUserId(Long userId);
    List<Alarm> findByUserIdAndReadStatus(Long userId, Boolean readStatus);
}
