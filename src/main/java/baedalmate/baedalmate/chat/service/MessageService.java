package baedalmate.baedalmate.chat.service;

import baedalmate.baedalmate.chat.dao.ChatRoomJpaRepository;
import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.chat.domain.Message;
import baedalmate.baedalmate.chat.dao.MessageJpaRepository;
import baedalmate.baedalmate.chat.domain.MessageType;
import baedalmate.baedalmate.fcm.event.ChatEvent;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.Fcm;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final MessageJpaRepository messageJpaRepository;
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long save(Message message) {
        messageJpaRepository.save(message);
        return message.getId();
    }

    @Transactional
    public Long createMessage(Long chatRoomId, Long userId, MessageType messageType, String msg) {
        User user = userJpaRepository.findById(userId).get();

        ChatRoom chatRoom = chatRoomJpaRepository.findById(chatRoomId).get();

        Message message = Message.createMessage(messageType, msg, user, chatRoom);

        messageJpaRepository.save(message);
        List<User> userList = chatRoom.getRecruit().getOrders().stream().map(o -> o.getUser()).collect(Collectors.toList());
        List<Fcm> fcmList = new ArrayList<>();
        for(User u : userList) {
            if(u.getId() == userId)
                continue;
            fcmList.addAll(u.getFcms());
        }
        eventPublisher.publishEvent(new ChatEvent(
                chatRoomId,
                chatRoom.getRecruit().getTitle(),
                msg,
                chatRoom.getRecruit().getImage(),
                fcmList));
        return message.getId();
    }
}
