package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.User;
import baedalmate.baedalmate.oauth.annotation.CurrentUser;
import baedalmate.baedalmate.oauth.domain.PrincipalDetails;
import baedalmate.baedalmate.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"유저 api"})
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @ApiOperation(value = "유저 정보 조회")
    @GetMapping(value = "/user")
    public UserDto getUserInfo(
            @CurrentUser PrincipalDetails principalDetails) {
        User user = userService.findOne(principalDetails.getId());
        return new UserDto(user.getNickname(), user.getProfileImage(), user.getAddress(), user.getDormitory());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserDto {
        private String nickname;
        private String profileImage;
        private String address;
        private String dormitory;
    }
}
