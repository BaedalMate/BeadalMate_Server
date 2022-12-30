package baedalmate.baedalmate.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class CreateReviewDto {
    @Schema(description = "모집글 id")
    private Long recruitId;
    private List<UserDto> users;
}
