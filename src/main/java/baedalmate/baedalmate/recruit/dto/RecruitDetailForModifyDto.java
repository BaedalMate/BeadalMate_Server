package baedalmate.baedalmate.recruit.dto;

import baedalmate.baedalmate.recruit.domain.Criteria;
import baedalmate.baedalmate.recruit.domain.Dormitory;
import baedalmate.baedalmate.recruit.domain.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "모집글 수정용 상세정보")
@NoArgsConstructor
@AllArgsConstructor
public class RecruitDetailForModifyDto {
    @Schema(description = "모집글 id", example = "1")
    private Long recruitId;
    @Schema(description = "카테고리 id", example = "1")
    private Long categoryId;
    @Schema(description = "배달지점명")
    private PlaceDto place;
    @Schema(description = "배달거점", example = "BURAM | KB | SUGLIM | NURI")
    private Dormitory dormitory;
    @Schema(description = "마감 기준", example = "NUMBER | PRICE | TIME")
    private Criteria criteria;
    @Schema(description = "최소주문금액", example = "20000")
    private int minPrice;
    @Schema(description = "최소 인원", example = "3")
    private int minPeople;
    @Schema(description = "배달팁")
    private List<ShippingFeeDto> shippingFee;
    @Schema(description = "쿠폰 사용 금액", example = "3000")
    private int coupon;
    @Schema(description = "배달앱", example = "BAEMIN | YOGIYO | COUPANG | DDANGYO | ETC")
    private Platform platform;
    @Schema(description = "마감 시간", example = "2020-12-24T16:28:27")
    private LocalDateTime deadlineDate;
    @Schema(description = "글 제목", example = "영계백숙")
    private String title;
    @Schema(description = "글 설명", example = "오오오오")
    private String description;
    @Schema(description = "무료 배달 여부", example = "true | false")
    private Boolean freeShipping;
    @Schema(description = "메뉴")
    private List<MenuDto> menu;
    @Schema(description = "태그")
    private List<TagDto> tags;
}
