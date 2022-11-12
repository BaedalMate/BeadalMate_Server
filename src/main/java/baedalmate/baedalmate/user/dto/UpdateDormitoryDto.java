package baedalmate.baedalmate.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class UpdateDormitoryDto {
    @Schema(description = "유저 거점 (SULIM | BURAM | SUNGLIM | KB | NURI")
    private String dormitory;
}
