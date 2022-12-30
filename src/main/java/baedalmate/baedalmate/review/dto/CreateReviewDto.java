package baedalmate.baedalmate.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema
public class CreateReviewDto {
    @Schema(description = "모집글 id")
    @NotNull
    private Long recruitId;
    @NotNull
    private List<UserDto> users;
}
