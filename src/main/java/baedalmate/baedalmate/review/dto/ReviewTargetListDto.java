package baedalmate.baedalmate.review.dto;

import baedalmate.baedalmate.recruit.dto.ParticipantDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class ReviewTargetListDto {
    @Schema(description = "모집글 id")
    private Long recruitId;
    @Schema(description = "참가자")
    private List<ReviewTargetDto> participants;
}
