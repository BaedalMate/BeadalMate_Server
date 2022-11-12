package baedalmate.baedalmate.recruit.dto;

import baedalmate.baedalmate.recruit.api.RecruitApiController;
import baedalmate.baedalmate.recruit.domain.Criteria;
import baedalmate.baedalmate.recruit.domain.Dormitory;
import baedalmate.baedalmate.recruit.domain.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "모집글 생성")
public class CreateRecruitDto {
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