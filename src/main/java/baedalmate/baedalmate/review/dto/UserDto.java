package baedalmate.baedalmate.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class UserDto {
    @Schema(description = "유저 id")
    private Long userId;
    @Schema(description = "후기 점수")
    private float score;
}
