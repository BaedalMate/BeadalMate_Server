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

    @Query("select f from Fcm f where f.fcmToken in :fcmList")
    List<Fcm> findByFcmTokenList(@Param("fcmList") List<String> fcmList);

    @Query("select f from Fcm f where f.user.id in :userIdList and allowRecruit = true")
    List<Fcm> findAllByUserIdListAndAllowRecruitTrue(@Param("userIdList") List<Long> userIdList);

    @Query("select f from Fcm f where f.user.id in :userIdList and allowChat = true")
    List<Fcm> findAllByUserIdListAndAllowChatTrue(@Param("userIdList") List<Long> userIdList);

    @Modifying(clearAutomatically = true)
    @Query("delete from Fcm f where f.deviceCode = :deviceCode and f.user.id = :userId")
    void deleteByDeviceCode(@Param("deviceCode") String deviceCode, @Param("userId") Long userId);
}
