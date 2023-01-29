package baedalmate.baedalmate.user.dao;

import baedalmate.baedalmate.user.domain.Fcm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FcmJpaRepository extends JpaRepository<Fcm, Long> {
    @Query("select f From Fcm f where f.user.id = :userId")
    List<Fcm> findAllByUserId(@Param("userId") Long userId);

    @Query("select f from Fcm f where f.user.id = :userId and f.deviceCode = :deviceCode")
    Fcm findAllByUserIdAndDeviceCode(@Param("userId") Long userId, @Param("deviceCode") String deviceCode);

    @Modifying(clearAutomatically = true)
    @Query("update Fcm f set f.fcmToken = :fcmToken where f.user.id = :userId and f.deviceCode = :deviceCode")
    void updateFcmTokenByUserIdAndDeviceCode(@Param("userId") Long userId, @Param("deviceCode") String deviceCode, @Param("fcmToken") String fcmToken);
}
