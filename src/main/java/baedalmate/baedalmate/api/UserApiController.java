package baedalmate.baedalmate.api;

import io.swagger.annotations.Api;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"유저 api"})
@RestController
public class UserApiController {
    public UserDto getUserInfo() {
        return new UserDto();
    }

    @Data
    @NoArgsConstructor
    static class UserDto {
        private String profileImage;
        private String nickname;
        private String address;
        private String dormitory;
    }
}
