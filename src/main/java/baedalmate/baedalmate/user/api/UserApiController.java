package baedalmate.baedalmate.user.api;

import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.user.dto.UpdateUserDto;
import baedalmate.baedalmate.user.service.UserService;
import baedalmate.baedalmate.user.dto.UserInfoDto;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Api(tags = {"유저 api"})
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @Value("${spring.servlet.multipart.location}")
    private String path;

    @ApiOperation(value = "유저 프로필 이미지 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "프로필 이미지 수정 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
    })
    @PutMapping(value = "/user/image")
    public ResponseEntity<Map<String, Object>> updateProfileImage(
            @AuthUser PrincipalDetails principalDetails,
            @RequestParam("uploadfile") MultipartFile uploadfile
    ) {
        String imageName = userService.updateProfileImage(principalDetails.getId(), uploadfile);
        Map<String, Object> response = new HashMap<>();
        response.put("image", imageName);
        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "유저 정보 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "프로필 이미지 수정 성공"),
            @ApiResponse(code = 400, message = "수정 실패: 닉네임 길이 제한"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
    })
    @PatchMapping(value = "/user")
    public ResponseEntity<UserInfoDto> updateUserInfo(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody UpdateUserDto updateUserDto
    ) {
        UserInfoDto userInfo = userService.update(principalDetails.getId(), updateUserDto.getNickname());
        return ResponseEntity.ok().body(userInfo);
    }

    @ApiOperation(value = "유저 정보 조회")
    @GetMapping(value = "/user")
    public ResponseEntity<UserInfoDto> getUserInfo(
            @AuthUser PrincipalDetails principalDetails) {
        UserInfoDto userInfo = userService.getUserInfo(principalDetails.getId());
        return ResponseEntity.ok().body(userInfo);
    }

    @ApiOperation(value = "유저 거점 변경")
    @PutMapping(value = "/user")
    public ResponseEntity<Map<String, Object>> setDormitory(
            @AuthUser PrincipalDetails principalDetails,
            @ApiParam(value = "유저 거점 (KB | SUNGLIM | SULIM | BULAM | NURI)")
            @RequestParam("dormitory") String dormitoryName
    ) {
        User user = userService.updateDormitory(principalDetails.getId(), dormitoryName);
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return ResponseEntity.ok().body(response);
    }
}
