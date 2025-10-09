package com.example.alert_module.history.repository;

import com.example.alert_module.history.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {
    List<AlertHistory> findAllByAlert_UserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<AlertHistory> findAllByAlert_Id(Long alertId);

}
