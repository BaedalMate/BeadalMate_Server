package baedalmate.baedalmate.chat.api;

import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.chat.domain.Message;
import baedalmate.baedalmate.chat.domain.MessageType;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.category.dto.MessageDto;
import baedalmate.baedalmate.chat.service.ChatRoomService;
import baedalmate.baedalmate.chat.service.MessageService;
import baedalmate.baedalmate.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = {"메시지 api"})
@RestController
@RequiredArgsConstructor
public class MessageApiController {

    private final SimpMessageSendingOperations sendingOperations;
    private final MessageService messageService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat/message")
    public void enter(@Valid MessageDto messageDto) {
//        if (MessageType.ENTER.equals(message.getType())) {
//            message.setMessage(message.getSender()+"님이 입장하였습니다.");
//        }
        // 유저 조회
        User user = userService.findOne(messageDto.getSenderId());
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomService.findOne(messageDto.getRoomId());

        // 메세지 db 저장
        messageService.createMessage(messageDto.getRoomId(), messageDto.getSenderId(), MessageType.TALK, messageDto.getMessage());
        messageDto.setSender(user.getNickname());
        messageDto.setSenderImage(user.getProfileImage());

        sendingOperations.convertAndSend("/topic/chat/room/" + messageDto.getRoomId(), messageDto);
    }
}