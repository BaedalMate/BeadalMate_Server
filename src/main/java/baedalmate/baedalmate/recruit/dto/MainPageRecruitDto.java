package baedalmate.baedalmate.recruit.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema
@AllArgsConstructor
public class MainPageRecruitDto {
    @Schema(description = "해당 모집글 id", example = "1")
    private Long recruitId;

    @Schema(description = "식당 이름", example = "도미노피자")
    private String place;

    @Schema(description = "최소 인원", example = "4")
    private int minPeople;

    @Schema(description = "현재 인원", example = "1")
    private int currentPeople;

    @Schema(description = "최소 금액", example = "20000")
    private int minPrice;

    @Schema(description = "글 작성 시간", example = "2022-12-11 11:11:11")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;

    @Schema(description = "마감 시간", example = "2022-12-11 11:11:11")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deadlineDate;

    @Schema(description = "유저 이름", example = "유상")
    private String username;

    @Schema(description = "유저 평점", example = "4.1")
    private float userScore;

    @Schema(description = "배달 거점", example = "수림학사")
    private String dormitory;

    @Schema(description = "배달비", example = "4000")
    private int shippingFee;

    @Schema(description = "모집글 이미지", example = "1234567.jpg")
    private String image;

    @Schema(description = "모집글 활성 여부", example = "true")
    private boolean active;
}