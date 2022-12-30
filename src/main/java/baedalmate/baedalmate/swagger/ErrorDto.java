package baedalmate.baedalmate.swagger;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class ErrorDto {
    public static String getExpiredJwt(){
        return "{\"code\": \"400\"}";
    }
}
