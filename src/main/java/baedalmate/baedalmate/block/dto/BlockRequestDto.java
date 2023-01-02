package baedalmate.baedalmate.block.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequestDto {
    @Schema(description = "차단(차단해제)할 유저 id", example = "3")
    private Long userId;
}
