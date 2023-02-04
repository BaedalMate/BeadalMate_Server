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
public class ChatRoomRecruitDetailDto {
    @Schema(description = "모집글 id", example = "1")
    private Long recruitId;
    @Schema(description = "모집글 이미지", example = "12345678.jpg")
    private String recruitImage;
    @Schema(description = "모집글 생성 시간", example = "2022-12-5 11:11:11")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;
    @Schema(description = "모집글 제목", example = "영계백숙")
    private String title;
    @Schema(description = "모집글 마감기준", example = "NUMBER | PRICE | TIME")
    private Criteria criteria;
    @Schema(description = "모집글 최소주문금액", example = "20000")
    private int minPrice;
    @Schema(description = "모집글 최소인원", example = "3")
    private int minPeople;
    @Schema(description = "모집글 마감날짜", example = "2022-12-5 11:11:11")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deadlineDate;
    @Schema(description = "모집글 비활성날짜", example = "2022-12-5 11:11:11")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deactivateDate;
    @Schema(description = "모집글 활성화 여부", example = "true | false")
    private boolean active;
    @Schema(description = "모집글 취소 여부", example = "true | false")
    private boolean cancel;
    @Schema(description = "모집글 취소 여부", example = "true | false")
    private boolean fail;
}