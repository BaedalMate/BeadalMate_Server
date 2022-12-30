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
@Schema(description = "권한 부족")
public class AccessDeniedErrorResponseDto {
    @Schema(example = "403")
    private String code;
    @Schema(example = "Access denied")
    private String message;
}
