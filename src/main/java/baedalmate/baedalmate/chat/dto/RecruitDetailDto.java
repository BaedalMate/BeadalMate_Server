package baedalmate.baedalmate.chat.dto;

import baedalmate.baedalmate.recruit.domain.Criteria;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class RecruitDetailDto {
    @Schema(description = "모집글 id")
    private Long recruitId;
    @Schema(description = "모집글 이미지")
    private String recruitImage;
    @Schema(description = "모집글 생성 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;
    @Schema(description = "모집글 제목")
    private String title;
    @Schema(description = "모집글 마감기준")
    private Criteria criteria;
    @Schema(description = "모집글 최소주문금액")
    private int minPrice;
    @Schema(description = "모집글 최소인원")
    private int minPeople;
    @Schema(description = "모집글 마감날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deadlineDate;
    @Schema(description = "모집글 활성화 여부")
    private boolean active;
}