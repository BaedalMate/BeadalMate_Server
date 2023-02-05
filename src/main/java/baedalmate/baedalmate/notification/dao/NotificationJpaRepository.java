package baedalmate.baedalmate.notification.dao;

import baedalmate.baedalmate.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
}
