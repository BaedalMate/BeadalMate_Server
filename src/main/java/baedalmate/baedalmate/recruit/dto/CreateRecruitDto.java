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
@Schema(description = "모집글 생성")
public class CreateRecruitDto {
    @Schema(description = "카테고리 id", example = "1")
    @NotNull
    private Long categoryId;
    @Schema(description = "배달지점명")
    @NotNull
    private PlaceDto place;
    @Schema(description = "배달거점", example = "BURAM | KB | SUGLIM | NURI")
    @NotNull
    private Dormitory dormitory;
    @Schema(description = "마감 기준", example = "NUMBER | PRICE | TIME")
    @NotNull
    private Criteria criteria;
    @Schema(description = "최소주문금액", example = "20000")
    @NotNull
    private Integer minPrice;
    @Schema(description = "최소 인원", example = "3")
    @NotNull
    private Integer minPeople;
    @Schema(description = "배달앱", example = "BAEMIN | YOGIYO | COUPANG | DDANGYO | ETC")
    @NotNull
    private Platform platform;
    @Schema(description = "마감 시간", example = "2020-12-24T16:28:27")
    @NotNull
    private LocalDateTime deadlineDate;
    @Schema(description = "글 제목", example = "영계백숙")
    @NotNull
    private String title;
    @Schema(description = "글 설명", example = "오오오오")
    private String description;
    @Schema(description = "무료 배달 여부", example = "true | false")
    @NotNull
    private Boolean freeShipping;
    @Schema(description = "예상 배달비", example = "1000")
    private Integer shippingFee;
    @Schema(description = "메뉴")
    private List<MenuDto> menu;
    @Schema(description = "태그")
    @NotNull
    private List<TagDto> tags;
}