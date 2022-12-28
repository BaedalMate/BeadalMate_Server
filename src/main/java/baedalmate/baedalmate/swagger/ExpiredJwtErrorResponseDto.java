package baedalmate.baedalmate.swagger;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@ApiModel(description = "Jwt 만료 상태")
public class ExpiredJwtErrorResponseDto {
    @ApiModelProperty(example = "401")
    private String code;
    @ApiModelProperty(example = "Expired access token")
    private String message;
}
