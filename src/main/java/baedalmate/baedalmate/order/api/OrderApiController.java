package baedalmate.baedalmate.order.api;

import baedalmate.baedalmate.chat.service.ChatRoomService;
import baedalmate.baedalmate.chat.service.MessageService;
import baedalmate.baedalmate.order.dto.OrderDto;
import baedalmate.baedalmate.order.dto.DeleteOrderDto;
import baedalmate.baedalmate.order.dto.OrderAndChatIdDto;
import baedalmate.baedalmate.order.service.OrderService;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "메뉴 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "수정 성공"),
            @ApiResponse(code = 400, message = "생성 실패: 필수 정보 누락"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 참여자가 아닌 경우")
    })
    @PutMapping(value = "/order")
    public ResponseEntity<Map<String, Object>> updateMenu(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody OrderDto createOrderDto
    ) {
        orderService.updateOrder(principalDetails.getId(), createOrderDto);
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "모집글 참여")
    @ApiResponses({
            @ApiResponse(code = 200, message = "참여 성공"),
            @ApiResponse(code = 400, message = "생성 실패: 필수 정보 누락"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 권한이 GUEST인 경우")
    })
    @PostMapping(value = "/order")
    public ResponseEntity<OrderAndChatIdDto> participate(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid OrderDto orderDto
    ) {

        OrderAndChatIdDto response = orderService.createOrder(principalDetails.getId(), orderDto);

        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "모집글 참여 취소")
    @ApiResponses({
            @ApiResponse(code = 200, message = "참여 취소 성공"),
            @ApiResponse(code = 400, message = "생성 실패: 필수 정보 누락"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 참여자가 아닌 경우")
    })
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
