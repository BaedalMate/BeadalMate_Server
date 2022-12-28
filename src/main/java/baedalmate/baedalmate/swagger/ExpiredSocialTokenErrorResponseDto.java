package baedalmate.baedalmate.swagger;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExpiredSocialTokenErrorResponseDto {
    @ApiModelProperty(example = "401")
    private String code;
    @ApiModelProperty(example = "Expired access token")
    private String message;
}
