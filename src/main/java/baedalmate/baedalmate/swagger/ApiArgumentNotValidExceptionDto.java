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
@ApiModel(description = "유효하지 않은 Request body")
public class ApiArgumentNotValidExceptionDto {
    @ApiModelProperty(example = "400")
    private String code;
    @ApiModelProperty(example = "Api request body invalid")
    private String message;
}
