package baedalmate.baedalmate.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"유저 api"})
@RestController
@RequestMapping("/api/v1")
public class UserApiController {

    @ApiOperation(value = "유저 정보 조회")
    @GetMapping(value = "/user")
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
