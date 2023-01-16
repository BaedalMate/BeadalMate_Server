package baedalmate.baedalmate.recruit.dto;

import baedalmate.baedalmate.user.dto.UserInfoDto;
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
    @Schema(description = "모집글 id", example = "1")
    private Long recruitId;
    @Schema(description = "모집글 이미지", example = "12341421.jpg")
    private String image;
    @Schema(description = "모집글 제목", example = "영계백숙")
    private String title;
    @Schema(description = "모집글 설명", example = "오오오오")
    private String description;
    @Schema(description = "배달 가게 정보")
    private PlaceDto place;
    @Schema(description = "배달 플랫폼(BAEMIN | YOGIYO | COUPANG | ETC)", example = "BAEMIN")
    private String platform;
    @Schema(description = "마감 시간", example = "2020-12-24T16:28:27")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deadlineDate;
    @Schema(description = "최소 배달비", example = "3000")
    private int shippingFee;
    @Schema(description = "배달비 상세")
    private List<ShippingFeeDto> shippingFeeDetail;
    @Schema(description = "쿠폰 사용 금액", example = "3000")
    private int coupon;
    @Schema(description = "모집 현재 인원", example = "2")
    private int currentPeople;
    @Schema(description = "모집 최소 인원", example = "3")
    private int minPeople;
    @Schema(description = "모집 현재 금액", example = "40000")
    private int currentPrice;
    @Schema(description = "모집 최소 금액", example = "30000")
    private int minPrice;
    @Schema(description = "거점(BURAM | KB | SUGLIM | NURI)", example = "NURI")
    private String dormitory;
    @Schema(description = "마감 여부(true | false)", example = "true")
    private boolean active;
    @Schema(description = "취소 여부(true | false)", example = "true")
    private boolean cancel;
    @Schema(description = "호스트 여부(true | false)", example = "true")
    private boolean host;
    @Schema(description = "참석 여부(true | false)", example = "true")
    private boolean participate;
    @Schema(description = "유저 정보")
    private UserInfoDto userInfo;

//    @Data
//    @AllArgsConstructor
//    public static class UserInfo {
//        @Schema(description = "유저 id", example = "1")
//        private Long userId;
//        @Schema(description = "유저 닉네임", example = "유상")
//        private String nickname;
//        @Schema(description = "유저 평점: (0~5 사이 실수값)", example = "4.5")
//        private float score;
//        @Schema(description = "유저 프로필 이미지", example = "12345.jpg")
//        private String profileImage;
//        @Schema(description = "유저거점(BURAM | KB | SUGLIM | NURI)", example = "NURI")
//        private String userDormitory;
//    }
}
