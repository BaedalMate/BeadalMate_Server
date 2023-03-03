package baedalmate.baedalmate.chat.api;

import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.chat.domain.Message;
import baedalmate.baedalmate.chat.domain.MessageType;
import baedalmate.baedalmate.fcm.event.ChatEvent;
import baedalmate.baedalmate.fcm.event.CloseEvent;
import baedalmate.baedalmate.notification.dao.NotificationJpaRepository;
import baedalmate.baedalmate.notification.domain.Notification;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.user.dao.FcmJpaRepository;
import baedalmate.baedalmate.user.domain.Fcm;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.category.dto.MessageDto;
import baedalmate.baedalmate.chat.service.ChatRoomService;
import baedalmate.baedalmate.chat.service.MessageService;
import baedalmate.baedalmate.user.service.RedisService;
import baedalmate.baedalmate.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "메시지 api")
@RestController
@RequiredArgsConstructor
public class MessageApiController {

    private final SimpMessageSendingOperations sendingOperations;
    private final MessageService messageService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final FcmJpaRepository fcmJpaRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderJpaRepository orderJpaRepository;
    private final RedisService redisService;

    @MessageMapping("/chat/message")
    public void message(@Valid MessageDto messageDto) {
//        if (MessageType.ENTER.equals(message.getType())) {
//            message.setMessage(message.getSender()+"님이 입장하였습니다.");
//        }

        // 유저 조회

        User user = userService.findOne(messageDto.getSenderId());
        if (messageDto.getType() == null) {
            messageDto.setSender(user.getNickname());
            messageDto.setSenderImage(user.getProfileImage());
            messageDto.setType(MessageType.TALK);
        }
        // 메세지 db 저장
        Long messageId = messageService.createMessage(messageDto.getRoomId(), messageDto.getSenderId(), messageDto.getType(), messageDto.getMessage(), messageDto.getReadMessageId());
        messageDto.setMessageId(messageId);
        sendingOperations.convertAndSend("/topic/chat/room/" + messageDto.getRoomId(), messageDto);
        if (MessageType.TALK.equals(messageDto.getType())) {
            // 채팅방 조회
            ChatRoom chatRoom = chatRoomService.findOne(messageDto.getRoomId());
            Recruit recruit = chatRoom.getRecruit();

            List<Long> userIdList = orderJpaRepository.findAllByRecruitIdUsingJoin(chatRoom.getRecruit().getId())
                    .stream().map(o -> o.getUser().getId()).collect(Collectors.toList());
            userIdList.remove(user.getId());
            List<Long> userIdListForFcm = new ArrayList<>();
            for (Long userId : userIdList) {
                String userValue = redisService.getValues(userId.toString());
                if ("list".equals(userValue) || messageDto.getRoomId().toString().equals(userValue)) {
                    sendingOperations.convertAndSend("/topic/chat/user/" + userId, messageDto);
                } else {
                    userIdListForFcm.add(userId);
                }
            }

            List<Fcm> fcmList = fcmJpaRepository.findAllByUserIdListAndAllowChatTrue(userIdListForFcm);
            eventPublisher.publishEvent(new ChatEvent(
                    recruit.getChatRoom().getId(),
                    recruit.getTitle(),
                    user.getNickname() + ": " + messageDto.getMessage(),
                    recruit.getImage(),
                    fcmList));
        }
    }
}