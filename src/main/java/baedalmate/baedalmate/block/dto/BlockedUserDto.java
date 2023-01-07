package baedalmate.baedalmate.block.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class BlockedUserDto {
    @Schema(description = "유저 id", example = "1")
    private Long userId;
    @Schema(description = "유저 닉네임", example = "유상")
    private String nickname;
    @Schema(description = "유저 프로필 이미지", example = "12344.jpg")
    private String profileImage;
}
