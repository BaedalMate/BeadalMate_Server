package baedalmate.baedalmate.order.api;

import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.chat.domain.Message;
import baedalmate.baedalmate.chat.domain.MessageType;
import baedalmate.baedalmate.chat.service.ChatRoomService;
import baedalmate.baedalmate.chat.service.MessageService;
import baedalmate.baedalmate.order.dto.CreateOrderDto;
import baedalmate.baedalmate.order.dto.DeleteOrderDto;
import baedalmate.baedalmate.order.dto.OrderAndChatIdDto;
import baedalmate.baedalmate.order.service.OrderService;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Api(tags = {"모집글 참여 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderApiController {

    private final OrderService orderService;
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;

    @ApiOperation(value = "모집글 참여")
    @PostMapping(value = "/order")
    public ResponseEntity<OrderAndChatIdDto> participate(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid CreateOrderDto createOrderDto
    ) {
        Long orderId = orderService.createOrder(principalDetails.getId(), createOrderDto);

        // chat room 조회
        ChatRoom chatRoom = chatRoomService.findByRecruitId(createOrderDto.getRecruitId());

        // message 생성
        Message message = Message.createMessage(MessageType.ENTER, "", user, chatRoom);
        messageService.save(message);

        OrderAndChatIdDto response = new OrderAndChatIdDto(orderId, chatRoom.getId());
        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "모집글 참여 취소")
    @DeleteMapping(value = "/order")
    public ResponseEntity<Map<String, Object>> cancelParticipate(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid DeleteOrderDto deleteOrderDto
    ) {
        orderService.deleteOrder(principalDetails.getId(), deleteOrderDto.getRecruitId());
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }
}
