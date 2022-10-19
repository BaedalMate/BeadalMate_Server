package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.ChatRoom;
import baedalmate.baedalmate.domain.Message;
import baedalmate.baedalmate.domain.MessageType;
import baedalmate.baedalmate.domain.User;
import baedalmate.baedalmate.dto.MessageDto;
import baedalmate.baedalmate.service.ChatRoomService;
import baedalmate.baedalmate.service.MessageService;
import baedalmate.baedalmate.service.UserService;
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
        Message message = Message.createMessage(MessageType.TALK, messageDto.getMessage(), user, chatRoom);
        messageService.save(message);
        messageDto.setSender(user.getNickname());
        messageDto.setSenderImage(user.getProfileImage());

        sendingOperations.convertAndSend("/topic/chat/room/"+messageDto.getRoomId(), messageDto);
    }
}