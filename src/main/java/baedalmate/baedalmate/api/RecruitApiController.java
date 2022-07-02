package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.oauth.annotation.CurrentUser;
import baedalmate.baedalmate.oauth.domain.PrincipalDetails;
import baedalmate.baedalmate.service.OrderService;
import baedalmate.baedalmate.service.RecruitService;
import baedalmate.baedalmate.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Api(tags = {"모집글 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RecruitApiController {

    private final UserService userService;
    private final RecruitService recruitService;
    private final OrderService orderService;

    @ApiOperation(value = "모집글 리스트 조회")
    @GetMapping(value = "/recruit/list")
    public Result getRecruitList(
            @ApiParam(value = "카테고리별 조회(일단 사용x)")
            @RequestParam(required = false) Long categoryId,
            @ApiParam(value = "예시: {ip}:8080/production/list?page=0&size=5&sort=view,DESC")
                    Pageable pageable
    ) {
        List<RecruitDto> collect = new ArrayList<>();
        return new Result(collect);
    }

    @PostMapping(value = "/recruit/new")
    public CreateRecruitResponse createRecruit(
            @CurrentUser PrincipalDetails principalDetails,
            @RequestBody CreateRecruitRequest createRecruitRequest
    ) {
        User user = userService.findOne(principalDetails.getId());
        // recruit 생성
        Recruit recruit = Recruit.createRecruit(
                user,
                createRecruitRequest.getMinPeople(),
                createRecruitRequest.getMinPrice(),
                createRecruitRequest.getDeadlineDate(),
                createRecruitRequest.getCriteria(),
                createRecruitRequest.getDormitory(),
                createRecruitRequest.getRestaurant(),
                createRecruitRequest.getPlatform(),
                createRecruitRequest.getCoupon(),
                createRecruitRequest.getDeliveryFee(),
                createRecruitRequest.getTitle(),
                createRecruitRequest.getDescription()
        );
        Long id = recruitService.createRecruit(recruit);
        // menu 생성
        List<Menu> menus = createRecruitRequest.getMenu().stream()
                .map(m -> Menu.createMenu(m.getName(), m.getPrice()))
                .collect(Collectors.toList());
        // order 생성
        Order order = Order.createOrder(user, recruit, menus);
        Long orderId = orderService.createOrder(order);
        return new CreateRecruitResponse(id);
    }

    @Data
    static class CreateRecruitRequest {
        private String restaurant;
        private Dormitory dormitory;
        private Criteria criteria;
        private int minPrice;
        private int minPeople;
        private int deliveryFee;
        private int coupon;
        private Platform platform;
        private LocalDateTime deadlineDate;
        private String title;
        private String description;
        private List<MenuDto> menu;
    }

    @Data
    @Schema
    static class MenuDto {
        private String name;
        private int price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CreateRecruitResponse {
        private Long id;
    }

    @Data
    @AllArgsConstructor
    @Schema
    static class Result {
        @Schema(name = "결과 리스트")
        private List<RecruitDto> data;
    }

    @Data
    @Schema
    static class RecruitDto {
        @Schema(name = "해당 모집글 id", example = "1")
        private Long id;

        @Schema(name = "식당 이름", example = "도미노피자")
        private String restaurantName;

        @Schema(name = "최소 주문 금액", example = "15000")
        private int minPrice;

        @Schema(name = "배달비", example = "3000")
        private int deliveryFee;

        @Schema(name = "글 작성 시간")
        private LocalDateTime createDate;

        @Schema(name = "마감 시간")
        private LocalDateTime deadlineDate;

        @Schema(name = "예상 배달 시간", example = "20~30분")
        private String estimateDeliveryTime;

        @Schema(name = "유저 평점", example = "4.1")
        private float userScore;
    }
}
