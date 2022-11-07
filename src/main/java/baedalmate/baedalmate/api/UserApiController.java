package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.User;
import baedalmate.baedalmate.dto.UserDto;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = {"유저 api"})
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @ApiOperation(value = "유저 정보 조회")
    @GetMapping(value = "/user")
    public UserInfoResponse getUserInfo(
            @AuthUser PrincipalDetails principalDetails) {
        User user = userService.findOne(principalDetails.getId());
        return new UserInfoResponse(user.getId(), user.getNickname(), user.getProfileImage(), user.getDormitoryName(), user.getScore());
    }

    @ApiOperation(value = "유저 거점 변경")
    @PutMapping(value = "/user")
    public Map<String, Object> setDormitory(
            @AuthUser PrincipalDetails principalDetails,
            @ApiParam(value = "유저 거점 (KB | SUNGLIM | SULIM | BULAM | NURI)")
            @RequestParam("dormitory") String dormitoryName
    ) {
        UserDto userDto = new UserDto();
        userDto.setDormitory(dormitoryName);
        User user = userService.update(principalDetails.getId(), userDto);
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return response;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserInfoResponse {
        @Schema(description = "유저 id")
        private Long userId;
        @Schema(description = "유저 닉네임")
        private String nickname;
        @Schema(description = "유저 프로필 이미지")
        private String profileImage;
        @Schema(description = "유저 거점")
        private String dormitory;
        @Schema(description = "유저 평점")
        private float score;
    }
}
