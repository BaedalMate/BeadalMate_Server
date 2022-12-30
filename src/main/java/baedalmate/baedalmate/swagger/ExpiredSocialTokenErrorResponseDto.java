package baedalmate.baedalmate.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "소셜 토큰 만료 상태")
public class ExpiredSocialTokenErrorResponseDto {
    @Schema(description = "Jwt 만료 상태")
    private String code;
    @Schema(example = "Expired social token")
    private String message;
}
