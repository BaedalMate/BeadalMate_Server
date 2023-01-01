package baedalmate.baedalmate.order.api;

import baedalmate.baedalmate.chat.service.ChatRoomService;
import baedalmate.baedalmate.chat.service.MessageService;
import baedalmate.baedalmate.order.dto.OrderDto;
import baedalmate.baedalmate.order.dto.DeleteOrderDto;
import baedalmate.baedalmate.order.dto.OrderAndChatIdDto;
import baedalmate.baedalmate.order.service.OrderService;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.swagger.AccessDeniedErrorResponseDto;
import baedalmate.baedalmate.swagger.ExpiredJwtErrorResponseDto;
import baedalmate.baedalmate.swagger.ResultSuccessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "모집글 참여 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
        @ApiResponse(description = "권한 부족", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessDeniedErrorResponseDto.class)))
})
public class OrderApiController {

    private final OrderService orderService;
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;

    @Operation(summary = "메뉴 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "참여 취소 성공",
                    content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 정보 누락",
                                            value = "{\"code\": 400, \"message\": \"Api request body invalid\"}"),
                                    @ExampleObject(name = "참여자가 아닐 경우",
                                    value = "{\"code\": 400, \"message\": \"User is not participant\"}")
                            }
                    )),
    })
    @PutMapping(value = "/order")
    public ResponseEntity<Map<String, Object>> updateMenu(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid OrderDto createOrderDto
    ) {
        orderService.updateOrder(principalDetails.getId(), createOrderDto);
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 참여")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "참여 성공"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 정보 누락",
                                            value = "{\"code\": 400, \"message\": \"Api request body invalid\"}"),
                                    @ExampleObject(name = "취소된 모집글",
                                            value = "{\"code\": 400, \"message\": \"Already canceled recruit\"}"),
                                    @ExampleObject(name = "마감된 모집글",
                                            value = "{\"code\": 400, \"message\": \"Already closed recruit\"}")
                            }
                    )),
    })
    @PostMapping(value = "/order")
    public ResponseEntity<OrderAndChatIdDto> participate(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid OrderDto orderDto
    ) {

        OrderAndChatIdDto response = orderService.createOrder(principalDetails.getId(), orderDto);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 참여 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "참여 취소 성공",
            content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 정보 누락", description = "필수 정보 누락",
                                            value = "{\"code\": 400, \"message\": \"Api request body invalid\"}"),
                                    @ExampleObject(name = "참여자가 아닐 경우", description = "취소된 모집글",
                                            value = "{\"code\": 400, \"message\": \"Already canceled recruit\"}"),
                                    @ExampleObject(name = "참여자가 아닐 경우", description = "마감된 모집글",
                                            value = "{\"code\": 400, \"message\": \"Already closed recruit\"}")
                            }
                    )),
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
