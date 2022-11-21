package baedalmate.baedalmate.recruit.dto;

import baedalmate.baedalmate.recruit.domain.Criteria;
import baedalmate.baedalmate.recruit.domain.Dormitory;
import baedalmate.baedalmate.recruit.domain.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "모집글 수정")
public class UpdateRecruitDto {
    @Schema(description = "카테고리 id")
    private Long categoryId;
    @Schema(description = "배달지점명")
    private PlaceDto place;
    @Schema(description = "배달거점 (BURAM|KB|SUGLIM|NURI)")
    private Dormitory dormitory;
    @Schema(description = "마감 기준 (NUMBER|PRICE|TIME)")
    private Criteria criteria;
    @Schema(description = "최소주문금액")
    private Integer minPrice;
    @Schema(description = "최소 인원")
    private Integer minPeople;
    @Schema(description = "배달팁")
    private List<ShippingFeeDto> shippingFee;
    @Schema(description = "쿠폰 사용 금액")
    private Integer coupon;
    @Schema(description = "배달앱 (BAEMIN|YOGIYO|COUPANG|ETC)")
    private Platform platform;
    @Schema(description = "마감 시간 (예시: 2020-12-24T16:28:27)")
    private LocalDateTime deadlineDate;
    @Schema(description = "글 제목")
    private String title;
    @Schema(description = "글 설명")
    private String description;
    @Schema(description = "무료 배달 여부")
    private Boolean freeShipping;
//    @Schema(description = "메뉴")
//    private List<MenuDto> menu;
    @Schema(description = "태그")
    private List<TagDto> tags;
}
