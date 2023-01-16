package baedalmate.baedalmate.recruit.dto;

import baedalmate.baedalmate.recruit.domain.Criteria;
import baedalmate.baedalmate.recruit.domain.Dormitory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class RecruitDto {
    @Schema(description = "해당 모집글 id", example = "1")
    private Long recruitId;

    @Schema(description = "식당 이름", example = "도미노피자")
    private String place;

    @Schema(description = "최소 인원", example = "4")
    private int minPeople;

    @Schema(description = "최소 주문 금액 ", example = "10000")
    private int minPrice;

    @Schema(description = "현재 인원", example = "1")
    private int currentPeople;

    @Schema(description = "현재 금액", example = "10000")
    private int currentPrice;

    @Schema(description = "마감 기준", example = "NUMBER | PRICE | TIME")
    private Criteria criteria;

    @Schema(description = "글 작성 시간", example = "2020-12-24 16:28:27")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;

    @Schema(description = "마감 시간", example = "2020-12-24 16:28:27")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deadlineDate;

    @Schema(description = "유저 평점 (0~5 사이 실수값)", example = "4.1")
    private float userScore;

    @Schema(description = "배달 거점", example = "수림학사")
    private String dormitory;

    @Schema(description = "모집글 제목", example = "영계백숙")
    private String title;

    @Schema(description = "모집글 이미지", example = "/images/123456.jpg")
    private String image;

    @Schema(description = "모집글 활성 여부", example = "true")
    private boolean active;

    public RecruitDto(Long recruitId, String place, int minPeople, int minPrice, int currentPeople, int currentPrice,
                      Criteria criteria, LocalDateTime createDate, LocalDateTime deadlineDate, float userScore, Dormitory dormitory,
                      String title, String image, boolean active) {
        this.recruitId = recruitId;
        this.place = place;
        this.minPeople = minPeople;
        this.minPrice = minPrice;
        this.currentPeople = currentPeople;
        this.currentPrice = currentPrice;
        this.criteria = criteria;
        this.createDate = createDate;
        this.deadlineDate = deadlineDate;
        this.userScore = userScore;
        this.dormitory = dormitory.getName();
        this.title = title;
        this.image = image;
        this.active = active;
    }
}