package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.dto.Result;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"모집글 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RecruitApiController {

    private final UserService userService;
    private final RecruitService recruitService;
    private final OrderService orderService;

    @ApiOperation(value = "모집글 생성")
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

    @ApiOperation(value = "모집글 리스트 조회")
    @GetMapping(value = "/recruit/list")
    public Result getRecruitList(
            @ApiParam(value = "카테고리별 조회(구현x)")
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10)
            @ApiParam(value = "예시: {ip}:8080/recruit/list?page=0&size=5&sort=createDate,DESC")
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "createDate", direction = Sort.Direction.DESC)
            })
                    Pageable pageable) {
        Page<Recruit> recruits = recruitService.findAll(pageable);

        List<RecruitDto> collect = recruits.getContent().stream()
                .map(r -> new RecruitDto(
                        r.getId(),
                        r.getRestaurant(),
                        r.getMinPrice(),
                        r.getMinPeople(),
                        r.getCurrentPeople(),
                        r.getDeliveryFee(),
                        r.getCreateDate(),
                        r.getDeadlineDate(),
                        r.getUser().getNickname(),
                        r.getUser().getScore(),
                        r.getDormitory().getName()
                ))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @Data
    @Schema
    static class CreateRecruitRequest {
        @Schema(name = "배달지점명")
        private String restaurant;
        @Schema(name = "배달거점 (BURAM|KB|SUGLIM|NURI)")
        private Dormitory dormitory;
        @Schema(name = "마감 기준 (NUMBER|PRICE|TIME")
        private Criteria criteria;
        @Schema(name = "최소주문금액")
        private int minPrice;
        @Schema(name = "최소 인원")
        private int minPeople;
        @Schema(name = "배달팁")
        private int deliveryFee;
        @Schema(name = "쿠폰 사용 금액")
        private int coupon;
        @Schema(name = "배달앱 (BAEMIN|YOGIYO|COUPANG)")
        private Platform platform;
        @Schema(name = "마감 시간 (예시: 2020-12-24T16:28:27)")
        private LocalDateTime deadlineDate;
        @Schema(name = "글 제목")
        private String title;
        @Schema(name = "글 설명")
        private String description;
        @Schema(name = "메뉴")
        private List<MenuDto> menu;
    }

    @Data
    @Schema
    static class MenuDto {
        @Schema(name = "메뉴명", example = "하와이안피자")
        private String name;
        @Schema(name = "가격", example = "15000")
        private int price;
    }

    @Data
    @Schema
    @NoArgsConstructor
    @AllArgsConstructor
    static class CreateRecruitResponse {
        @Schema(name = "Recruit id")
        private Long id;
    }

    @Data
    @Schema
    @NoArgsConstructor
    @AllArgsConstructor
    static class RecruitDto {
        @Schema(name = "해당 모집글 id", example = "1")
        private Long id;

        @Schema(name = "식당 이름", example = "도미노피자")
        private String restaurantName;

        @Schema(name = "최소 주문 금액", example = "15000")
        private int minPrice;

        @Schema(name = "최소 인원", example = "4")
        private int minPeople;

        @Schema(name = "현재 인언", example = "1")
        private int currentPeople;

        @Schema(name = "배달비", example = "3000")
        private int deliveryFee;

        @Schema(name = "글 작성 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createDate;

        @Schema(name = "마감 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime deadlineDate;

        @Schema(name = "유저 이름", example = "유상")
        private String username;

        @Schema(name = "유저 평점", example = "4.1")
        private float userScore;

        private String dormitory;
    }
}
