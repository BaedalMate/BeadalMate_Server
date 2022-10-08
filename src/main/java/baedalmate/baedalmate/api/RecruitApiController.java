package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.domain.embed.Place;
import baedalmate.baedalmate.oauth.annotation.CurrentUser;
import baedalmate.baedalmate.oauth.domain.PrincipalDetails;
import baedalmate.baedalmate.repository.CategoryRepository;
import baedalmate.baedalmate.service.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final CategoryRepository categoryRepository;
    private final MenuService menuService;
    private final CategoryImageService categoryImageService;

    @ApiOperation(value = "모집글 생성")
    @PostMapping(value = "/recruit/new")
    public CreateRecruitResponse createRecruit(
            @CurrentUser PrincipalDetails principalDetails,
            @RequestBody @Valid CreateRecruitRequest createRecruitRequest
    ) {
        // 유저 정보 조회
        User user = userService.findOne(principalDetails.getId());

        // 태그 생성
        List<Tag> tags;
        if(createRecruitRequest.getTags().size()>0) {
            tags = createRecruitRequest
                    .getTags()
                    .stream()
                    .map(m -> Tag.createTag(m.getTagname()))
                    .collect(Collectors.toList());
        } else {
            tags = new ArrayList<>();
        }

        // 배달비 생성
        List<ShippingFee> shippingFees;
        if(createRecruitRequest.isFreeShipping()) { // 무료배달이면 shippingFees는 빈 ArrayList
            shippingFees = new ArrayList<>();
        }
        else {
            shippingFees = createRecruitRequest.getShippingFee().stream()
                    .map(m -> ShippingFee.createShippingFee(m.getShippingFee(), m.getLowerPrice(), m.getUpperPrice()))
                    .collect(Collectors.toList());
        }
        // place 생성
        PlaceDto placeDto = createRecruitRequest.getPlace();
        Place place = Place.createPlace(placeDto.getName(), placeDto.getAddressName(), placeDto.getRoadAddressName(), placeDto.getX(), placeDto.getY());

        // Category 조회
        Category category = categoryRepository.findOne(createRecruitRequest.getCategoryId());

        // 랜덤 카테고리 이미지 조회
        CategoryImage categoryImage = categoryImageService.getRandomCategoryImage(category);

        // recruit 생성
        Recruit recruit = Recruit.createRecruit(
                user,
                category,
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
                categoryImage.getName(),
                createRecruitRequest.isFreeShipping(),
                shippingFees,
                tags
        );
        Long id = recruitService.createRecruit(recruit);

        // order 생성
        Order order = Order.createOrder(user);
        orderService.createOrder(recruit, order);

        // menu 생성
        List<Menu> menus = createRecruitRequest.getMenu().stream()
                .map(m -> Menu.createMenu(m.getName(), m.getPrice(), m.getQuantity()))
                .collect(Collectors.toList());

        for(MenuDto menuDto : createRecruitRequest.getMenu()) {
            menuService.createMenu(order, Menu.createMenu(menuDto.getName(), menuDto.getPrice(), menuDto.getQuantity()));
        }

        return new CreateRecruitResponse(id);
    }

    @ApiOperation(value = "모집글 리스트 조회")
    @GetMapping(value = "/recruit/list")
    public RecruitList getRecruitList(
            @ApiParam(value = "카테고리별 조회")
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10)
            @ApiParam(value = "예시: {ip}:8080/recruit/list?page=0&size=5&sort=deadlineDate&categoryId=1")
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            })
                    Pageable pageable) {

        List<Recruit> recruits;
        if(categoryId == null) {
            recruits = recruitService.findAll(pageable);
        } else {
            recruits = recruitService.findAllByCategory(categoryId, pageable);
        }
        List<RecruitDto> collect = recruits.stream()
                .map(r -> new RecruitDto(
                        r.getId(),
                        r.getPlace().getName(),
                        r.getMinPeople(),
                        r.getMinPrice(),
                        r.getCurrentPeople(),
                        r.getCriteria(),
                        r.getCreateDate(),
                        r.getDeadlineDate(),
                        r.getUser().getScore(),
                        r.getDormitory().getName(),
                        r.getTitle(),
                        r.getImage()
                ))
                .collect(Collectors.toList());
        return new RecruitList(collect);
    }

    @ApiOperation(value = "메인페이지 모집글 리스트 조회")
    @GetMapping(value = "/recruit/main/list")
    public MainRecruitList getMainRecruitList(
            @PageableDefault(size = 5)
            @ApiParam(value = "예시: {ip}:8080/recruit/main/list?page=0&size=5&sort=deadlineDate")
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            }) Pageable pageable) {
        List<Recruit> recruits = recruitService.findAll(pageable);
        List<MainRecruitDto> collect = recruits.stream()
                .map(r -> new MainRecruitDto(
                            r.getId(),
                            r.getPlace().getName(),
                            r.getMinPeople(),
                            r.getCurrentPeople(),
                            r.getMinPrice(),
                            r.getCreateDate(),
                            r.getDeadlineDate(),
                            r.getUser().getNickname(),
                            r.getUser().getScore(),
                            r.getDormitory().getName(),
                            r.getMinShippingFee(),
                            r.getImage()
                        )
                )
                .collect(Collectors.toList());
        return new MainRecruitList(collect);
    }

    @ApiOperation(value = "메인페이지 태그 포함된 모집글 리스트 조회")
    @GetMapping(value = "/recruit/tag/list")
    public TagRecruitList getTagRecruitList(
            @CurrentUser PrincipalDetails principalDetails,
            @PageableDefault(size = 5)
            @ApiParam(value = "예시: {ip}:8080/recruit/tag/list?page=0&size=5&sort=deadlineDate")
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            }) Pageable pageable) {
        // 유저 정보 조회
        User user = userService.findOne(principalDetails.getId());
        if(user.getDormitory() == null) {
            // throw exception
        }
        List<Recruit> recruits = recruitService.findAllWithTag(user.getDormitory(), pageable);
        List<TagRecruitDto> collect = recruits.stream()
                .map(r -> new TagRecruitDto(
                        r.getId(),
                        r.getPlace().getName(),
                        r.getMinPrice(),
                        r.getCreateDate(),
                        r.getDeadlineDate(),
                        r.getUser().getNickname(),
                        r.getUser().getScore(),
                        r.getDormitory().getName(),
                        r.getMinShippingFee(),
                        r.getTags().stream().map(t -> new TagDto(t.getName())).collect(Collectors.toList()),
                        r.getImage()
                ))
                .collect(Collectors.toList());
        return new TagRecruitList(collect);
    }

    @ApiOperation(value = "모집글 상세 조회")
    @GetMapping(value = "/recruit/{id}")
    public RecruitDetail getRecruit(
            @CurrentUser PrincipalDetails principalDetails,
            @ApiParam(value = "모집글 id")
            @PathVariable("id")
            Long recruitId
        ) {
        // 유저 조회
        User user = principalDetails.getUser();

        // Recruit 조회
        Recruit recruit = recruitService.findById(recruitId);

        // Recruit 조회수 증가
        int view = recruitService.updateView(recruitId);

        // PlaceDto 생성
        Place place = recruit.getPlace();
        PlaceDto placeDto = new PlaceDto(
                place.getName(),
                place.getAddressName(),
                place.getRoadAddressName(),
                place.getX(),
                place.getY()
        );

        // ShippingFeeDetail 생성
        List<ShippingFeeDto> shippingFeeDetails = recruit.getShippingFees()
                .stream().map(s -> new ShippingFeeDto(
                            s.getShippingFee(),
                            s.getLowerPrice(),
                            s.getUpperPrice()
                    )
                )
                .collect(Collectors.toList());

        boolean host = recruit.getUser().getId() == user.getId() ? true : false;
        boolean participate = false;
        for(Order order : recruit.getOrders()) {
            if(order.getUser().getId() == user.getId()) {
                participate = true;
            }
        }

        User hostUser = recruit.getUser();

        return new RecruitDetail(
                recruit.getId(),
                recruit.getImage(),
                recruit.getTitle(),
                recruit.getDescription(),
                placeDto,
                recruit.getPlatform().name(),
                recruit.getDeadlineDate(),
                recruit.getMinShippingFee(),
                shippingFeeDetails,
                recruit.getCoupon(),
                recruit.getCurrentPeople(),
                recruit.getMinPeople(),
                recruit.getDormitory().getName(),
                hostUser.getNickname(),
                hostUser.getScore(),
                hostUser.getProfileImage(),
                hostUser.getDormitoryName(),
                recruit.isActive(),
                host,
                participate
        );
    }

    @Data
    @Schema
    @NoArgsConstructor
    @AllArgsConstructor
    static class RecruitDetail {
        @Schema(description = "모집글 id")
        private Long recruitId;
        @Schema(description = "모집글 이미지")
        private String image;
        @Schema(description = "모집글 제목")
        private String title;
        @Schema(description = "모집글 설명")
        private String description;
        @Schema(description = "배달 가게 정보")
        private PlaceDto place;
        @Schema(description = "배달 플랫폼")
        private String platform;
        @Schema(description = "마감 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime deadlineDate;
        @Schema(description = "최소 배달비")
        private int shippingFee;
        @Schema(description = "배달비 상세")
        private List<ShippingFeeDto> shippingFeeDetail;
        @Schema(description = "쿠폰 사용 금액")
        private int coupon;
        @Schema(description = "모집 현재 인원")
        private int currentPeople;
        @Schema(description = "모집 최소 인원")
        private int minPeople;
        @Schema(description = "거점")
        private String dormitory;
        @Schema(description = "유저 이름")
        private String username;
        @Schema(description = "유저 평점")
        private float score;
        @Schema(description = "유저 프로필 이미지")
        private String profileImage;
        @Schema(description = "유저 거점")
        private String userDormitory;
        @Schema(description = "마감 여부")
        private boolean active;
        @Schema(description = "호스트 여부")
        private boolean host;
        @Schema(description = "참석 여부")
        private boolean participate;
    }

    @Data
    @Schema
    @NoArgsConstructor
    @AllArgsConstructor
    static class TagRecruitList {
        private List<TagRecruitDto> recruitList;
    }

    @Data
    @Schema
    @AllArgsConstructor
    static class TagRecruitDto {
        @Schema(description = "해당 모집글 id", example = "1")
        private Long id;

        @Schema(description = "식당 이름", example = "도미노피자")
        private String place;

        @Schema(description = "최소 금액", example = "15000")
        private int minPrice;

        @Schema(description = "글 작성 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createDate;

        @Schema(description = "마감 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime deadlineDate;

        @Schema(description = "유저 이름", example = "유상")
        private String username;

        @Schema(description = "유저 평점", example = "4.1")
        private float userScore;

        @Schema(description = "배달 거점", example = "수림학사")
        private String dormitory;

        @Schema(description = "배달비")
        private int shippingFee;

        @Schema(description = "태그")
        private List<TagDto> tags;

        @Schema(description = "모집글 이미지")
        private String image;
    }

    @Data
    @Schema
    @AllArgsConstructor
    static class MainRecruitList {
        private List<MainRecruitDto> recruitList;
    }

    @Data
    @Schema
    @AllArgsConstructor
    static class MainRecruitDto {
        @Schema(description = "해당 모집글 id", example = "1")
        private Long id;

        @Schema(description = "식당 이름", example = "도미노피자")
        private String place;

        @Schema(description = "최소 인원", example = "4")
        private int minPeople;

        @Schema(description = "현재 인원", example = "1")
        private int currentPeople;

        @Schema(description = "최소 금액")
        private int minPrice;

        @Schema(description = "글 작성 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createDate;

        @Schema(description = "마감 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime deadlineDate;

        @Schema(description = "유저 이름", example = "유상")
        private String username;

        @Schema(description = "유저 평점", example = "4.1")
        private float userScore;

        @Schema(description = "배달 거점", example = "수림학사")
        private String dormitory;

        @Schema(description = "배달비")
        private int shippingFee;

        @Schema(description = "모집글 이미지")
        private String image;
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
        @Schema(description = "카테고리 id")
        @NotNull
        private Long categoryId;
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
        @NotNull
        private List<TagDto> tags;
    }

    @Data
    @Schema
    @NoArgsConstructor
    @AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
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
        @Schema(description = "수량", example = "1")
        private int quantity;
    }

   @Data
   @Schema
   @NoArgsConstructor
   @AllArgsConstructor
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

        @Schema(description = "최소 주문 금액 ", example = "10000")
        private int minPrice;

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

        @Schema(description = "모집글 이미지")
        private String image;
    }
}
