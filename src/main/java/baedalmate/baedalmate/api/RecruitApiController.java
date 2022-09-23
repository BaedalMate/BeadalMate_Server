package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.domain.embed.Place;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
            @RequestBody @Valid CreateRecruitRequest createRecruitRequest
    ) {
        // 유저 정보 조회
        User user = userService.findOne(principalDetails.getId());
        // menu 생성
        List<Menu> menus = createRecruitRequest.getMenu().stream()
                .map(m -> Menu.createMenu(m.getName(), m.getPrice()))
                .collect(Collectors.toList());
        // order 생성
        Order order = Order.createOrder(user, menus);
        Long orderId = orderService.createOrder(order);
        // 태그 생성
        List<Tag> tags = createRecruitRequest.getTag().stream()
                .map(m -> Tag.createTag(m.getTagname()))
                .collect(Collectors.toList());
        // 배달비 생성
        List<ShippingFee> shippingFees = createRecruitRequest.getShippingFee().stream()
                .map(m -> ShippingFee.createShippingFee(m.getShippingFee(), m.getLowerPrice(), m.getUpperPrice()))
                .collect(Collectors.toList());
        // place 생성
        PlaceDto placeDto = createRecruitRequest.getPlace();
        Place place = Place.createPlace(placeDto.getName(), placeDto.getAddressName(), placeDto.getRoadAddressName(), placeDto.getX(), placeDto.getY());
        // recruit 생성
        Recruit recruit = Recruit.createRecruit(
                user,
                createRecruitRequest.getMinPeople(),
                createRecruitRequest.getMinPrice(),
                createRecruitRequest.getDeadlineDate(),
                createRecruitRequest.getCriteria(),
                createRecruitRequest.getDormitory(),
                place,
                createRecruitRequest.getPlatform(),
                createRecruitRequest.getCoupon(),
                createRecruitRequest.getTitle(),
                createRecruitRequest.getDescription(),
                createRecruitRequest.isFreeShipping(),
                shippingFees,
                order,
                tags
        );
        Long id = recruitService.createRecruit(recruit);
        return new CreateRecruitResponse(id);
    }

    @ApiOperation(value = "모집글 리스트 조회")
    @GetMapping(value = "/recruit/list")
    public RecruitList getRecruitList(
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
                        r.getPlace().getName(),
                        r.getMinPeople(),
                        r.getCurrentPeople(),
                        r.getCriteria(),
                        r.getCreateDate(),
                        r.getDeadlineDate(),
                        r.getUser().getScore(),
                        r.getDormitory().getName(),
                        r.getTitle()
                ))
                .collect(Collectors.toList());
        return new RecruitList(collect);
    }

    @Data
    @Schema
    @AllArgsConstructor
    static class RecruitList {
        private List<RecruitDto> recruitList;
    }

    @Data
    @Schema(description = "모집글 생성")
    static class CreateRecruitRequest {
        @Schema(description = "배달지점명")
        @NotNull
        private PlaceDto place;
        @Schema(description = "배달거점 (BURAM|KB|SUGLIM|NURI)")
        @NotNull
        private Dormitory dormitory;
        @Schema(description = "마감 기준 (NUMBER|PRICE|TIME)")
        @NotNull
        private Criteria criteria;
        @Schema(description = "최소주문금액")
        @NotNull
        private int minPrice;
        @Schema(description = "최소 인원")
        @NotNull
        private int minPeople;
        @Schema(description = "배달팁")
        @NotNull
        private List<ShippingFeeDto> shippingFee;
        @Schema(description = "쿠폰 사용 금액")
        @NotNull
        private int coupon;
        @Schema(description = "배달앱 (BAEMIN|YOGIYO|COUPANG|ETC)")
        @NotNull
        private Platform platform;
        @Schema(description = "마감 시간 (예시: 2020-12-24T16:28:27)")
        @NotNull
        private LocalDateTime deadlineDate;
        @Schema(description = "글 제목")
        @NotNull
        private String title;
        @Schema(description = "글 설명")
        private String description;
        @Schema(description = "무료 배달 여부")
        @NotNull
        private boolean freeShipping;
        @Schema(description = "메뉴")
        private List<MenuDto> menu;
        @Schema(description = "태그")
        private List<TagDto> tag;
    }

    @Data
    @Schema
    static class PlaceDto {
        @Schema(description = "장소명")
        private String name;// 장소명, 업체명
        @Schema(description = "전체 지번 주소")
        private String addressName; // 전체 지번 주소
        @Schema(description = "전체 도로명 주소")
        private String roadAddressName; // 전체 도로명 주소
        @Schema(description = "x 좌표값 or longitude")
        private float x; // X 좌표값 혹은 longitude
        @Schema(description = "y 좌표값 or latitude")
        private float y; // Y 좌표값 혹은 latitude
    }

    @Data
    @Schema
    static class TagDto {
        @Schema(description = "태그명")
        private String tagname;
    }

    @Data
    @Schema
    static class MenuDto {
        @Schema(description = "메뉴명", example = "하와이안피자")
        private String name;
        @Schema(description = "가격", example = "15000")
        private int price;
    }

   @Data
   @Schema
   static class ShippingFeeDto {
       @Schema(description = "배달비")
       private int shippingFee;
       @Schema(description = "해당 가격 이상")
       private int lowerPrice;
       @Schema(description = "해당 가격 이하")
       private int upperPrice;
   }

    @Data
    @Schema
    @NoArgsConstructor
    @AllArgsConstructor
    static class CreateRecruitResponse {
        @Schema(description = "Recruit id")
        private Long id;
    }

    @Data
    @Schema
    @NoArgsConstructor
    @AllArgsConstructor
    static class RecruitDto {
        @Schema(description = "해당 모집글 id", example = "1")
        private Long id;

        @Schema(description = "식당 이름", example = "도미노피자")
        private String place;

        @Schema(description = "최소 인원", example = "4")
        private int minPeople;

        @Schema(description = "현재 인언", example = "1")
        private int currentPeople;

        @Schema(description = "마감 기준")
        private Criteria criteria;

        @Schema(description = "글 작성 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createDate;

        @Schema(description = "마감 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime deadlineDate;

        @Schema(description = "유저 평점", example = "4.1")
        private float userScore;

        @Schema(description = "배달 거점", example = "수림학사")
        private String dormitory;

        @Schema(description = "모집글 제목", example = "글 제목")
        private String title;
    }
}
