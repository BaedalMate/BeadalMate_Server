package baedalmate.baedalmate.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class UpdateUserDto {
    @Schema(description = "유저 닉네임")
    private String nickname;
}
