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
@Schema(description = "Jwt 만료 상태")
public class ExpiredJwtErrorResponseDto {
    @Schema(example = "401")
    private String code;
    @Schema(example = "Token expired")
    private String message;
}
