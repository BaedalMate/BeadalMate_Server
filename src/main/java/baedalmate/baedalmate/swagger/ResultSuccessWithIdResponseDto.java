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
@Schema
public class ResultSuccessWithIdResponseDto {
    @Schema(example = "success")
    private String result;
    @Schema(example = "3")
    private String id;
}
