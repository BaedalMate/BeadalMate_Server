package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.oauth.annotation.CurrentUser;
import baedalmate.baedalmate.oauth.domain.PrincipalDetails;
import baedalmate.baedalmate.repository.OrderJpaRepository;
import baedalmate.baedalmate.repository.OrderRepository;
import baedalmate.baedalmate.service.MenuService;
import baedalmate.baedalmate.service.OrderService;
import baedalmate.baedalmate.service.RecruitService;
import baedalmate.baedalmate.service.UserService;
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
    private final OrderJpaRepository orderJpaRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final RecruitService recruitService;

    @ApiOperation(value = "모집글 참여")
    @PostMapping(value = "/order")
    public CreateOrderResponse createOrder(
            @CurrentUser PrincipalDetails principalDetails,
            @RequestBody @Valid CreateOrderRequest createOrderRequest
    ) {
        // 유저 정보 조회
        User user = userService.findOne(principalDetails.getId());

        Order order = Order.createOrder(user);

        Recruit recruit = recruitService.findById(createOrderRequest.getRecruitId());

        orderService.createOrder(recruit, order);

        for(MenuDto menuDto : createOrderRequest.getMenu()) {
            Menu menu = Menu.createMenu(menuDto.getName(), menuDto.getPrice(), menuDto.getQuantity());
            menuService.createMenu(order, menu);
        }

        Message message = Message.createMessage(MessageType.ENTER, "", user, recruit.getChatRoom());

        return new CreateOrderResponse(order.getId());
    }

    @Data
    @Schema
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
        private Long id;
    }
}
