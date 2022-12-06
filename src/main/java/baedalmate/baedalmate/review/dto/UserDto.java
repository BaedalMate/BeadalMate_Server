package baedalmate.baedalmate.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class UserDto {
    @Schema(description = "유저 id", example = "1")
    private Long userId;
    @Schema(description = "후기 점수 (0~5 사이 실수)", example = "4.5")
    private float score;
}
