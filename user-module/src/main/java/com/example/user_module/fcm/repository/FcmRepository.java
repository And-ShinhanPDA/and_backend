package com.example.user_module.fcm.repository;

import com.example.user_module.fcm.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FcmRepository extends JpaRepository<FcmToken, Long> {
    List<FcmToken> findByUserIdAndActivedTrue(Long userId);


    @Transactional
    @Modifying
    @Query("""
        UPDATE FcmToken f
        SET f.actived = false
        WHERE f.user.id = :userId
          AND f.deviceId = :deviceId
    """)
    void deactivateToken(Long userId, String deviceId);

    @Transactional
    @Modifying
    @Query("""
        UPDATE FcmToken f
        SET f.actived = true
        WHERE f.user.id = :userId
          AND f.deviceId = :deviceId
    """)
    void activateToken(Long userId, String deviceId);
}
