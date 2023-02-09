package baedalmate.baedalmate.notification.service;

import baedalmate.baedalmate.notification.dao.NotificationJpaRepository;
import baedalmate.baedalmate.notification.domain.Notification;
import baedalmate.baedalmate.notification.dto.NotificationDto;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationJpaRepository notificationJpaRepository;
    private final UserJpaRepository userJpaRepository;
    public List<NotificationDto> notificationList(Long userId) {
        List<Notification> notifications = notificationJpaRepository.findAllByUserId(userId);
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(n -> new NotificationDto(n.getTitle(),n.getBody(),n.getImage(),n.getChatRoomId(), n.getCreateDate()))
                .collect(Collectors.toList());
        return notificationDtos;
    }
}
