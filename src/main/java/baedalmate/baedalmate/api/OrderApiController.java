package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.oauth.annotation.CurrentUser;
import baedalmate.baedalmate.oauth.domain.PrincipalDetails;
import baedalmate.baedalmate.repository.OrderJpaRepository;
import baedalmate.baedalmate.repository.OrderRepository;
import baedalmate.baedalmate.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = {"모집글 참여 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderApiController {

    private final MenuService menuService;
    private final OrderService orderService;
    private final UserService userService;
    private final RecruitService recruitService;
    private final MessageService messageService;

    @ApiOperation(value = "모집글 참여")
    @PostMapping(value = "/order")
    public CreateOrderResponse createOrder(
            @CurrentUser PrincipalDetails principalDetails,
            @RequestBody @Valid CreateOrderRequest createOrderRequest
    ) {
        // 유저 정보 조회
        User user = userService.findOne(principalDetails.getId());

        // 주문 생성
        Order order = Order.createOrder(user);

        // 모집글 조회
        Recruit recruit = recruitService.findOne(createOrderRequest.getRecruitId());

        orderService.createOrder(recruit, order);

        // 메뉴 생성
        for(MenuDto menuDto : createOrderRequest.getMenu()) {
            Menu menu = Menu.createMenu(menuDto.getName(), menuDto.getPrice(), menuDto.getQuantity());
            menuService.createMenu(order, menu);
        }

        orderService.updateCurrentPrice(order);
        recruitService.updateCurrentPeople(recruit);

        // chat room 조회
        ChatRoom chatRoom = recruit.getChatRoom();

        // message 생성
        Message message = Message.createMessage(MessageType.ENTER, "", user, chatRoom);
        messageService.save(message);
        return new CreateOrderResponse(order.getId(), chatRoom.getId());
    }

    @Data
    @Schema
    @NoArgsConstructor
    @AllArgsConstructor
    static class CreateOrderRequest {
        @Schema(description = "모집글 id")
        @NotNull
        private Long recruitId;
        @Schema(description = "메뉴")
        @NotNull
        private List<MenuDto> menu;
    }

    @Data
    @Schema
    @NoArgsConstructor
    @AllArgsConstructor
    static class MenuDto {
        @Schema(description = "메뉴명", example = "하와이안피자")
        private String name;
        @Schema(description = "가격", example = "15000")
        private int price;
        @Schema(description = "수량", example = "1")
        private int quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CreateOrderResponse {
        @Schema(description = "주문 id", example = "1")
        private Long id;
        @Schema(description = "채팅방 id", example = "1")
        private Long chatRoomId;
    }
}
