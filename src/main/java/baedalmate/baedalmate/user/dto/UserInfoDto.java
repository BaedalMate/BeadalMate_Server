package baedalmate.baedalmate.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class UserInfoDto {
    @Schema(description = "유저 id")
    private Long userId;
    @Schema(description = "유저 닉네임")
    private String nickname;
    @Schema(description = "유저 프로필 이미지")
    private String profileImage;
    @Schema(description = "유저 거점")
    private String dormitory;
    @Schema(description = "유저 평점")
    private float score;
}
