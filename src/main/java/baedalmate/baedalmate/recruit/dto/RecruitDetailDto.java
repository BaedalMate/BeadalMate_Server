package baedalmate.baedalmate.recruit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class RecruitDetailDto {
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
    @Schema(description = "모집 현재 금액")
    private int currentPrice;
    @Schema(description = "모집 최소 금액")
    private int minPrice;
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
    @Schema(description = "취소 여부")
    private boolean cancel;
    @Schema(description = "호스트 여부")
    private boolean host;
    @Schema(description = "참석 여부")
    private boolean participate;
}
