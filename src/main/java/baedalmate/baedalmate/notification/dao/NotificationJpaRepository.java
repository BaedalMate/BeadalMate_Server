package baedalmate.baedalmate.notification.dao;

import baedalmate.baedalmate.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
    @Query("select n from Notification n where n.user.id = :userId order by n.createDate desc")
    List<Notification> findAllByUserId(@Param("userId") Long userId);
}
