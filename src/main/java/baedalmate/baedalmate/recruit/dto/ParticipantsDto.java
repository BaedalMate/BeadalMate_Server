package baedalmate.baedalmate.recruit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantsDto {
    @Schema(description = "모집글 id")
    private Long recruitId;
    @Schema(description = "참가자")
    private List<ParticipantDto> participants;
}
