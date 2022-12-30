package baedalmate.baedalmate.swagger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ApiArgumentNotValidExceptionDto {
    private String code;
    private String message;
}
