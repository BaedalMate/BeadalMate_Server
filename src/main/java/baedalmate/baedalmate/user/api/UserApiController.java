package baedalmate.baedalmate.user.api;

import baedalmate.baedalmate.swagger.AccessDeniedErrorResponseDto;
import baedalmate.baedalmate.swagger.ExpiredJwtErrorResponseDto;
import baedalmate.baedalmate.swagger.ResultSuccessResponseDto;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.user.dto.UpdateUserDto;
import baedalmate.baedalmate.user.service.UserService;
import baedalmate.baedalmate.user.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "유저 api")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
        @ApiResponse(description = "권한 부족", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessDeniedErrorResponseDto.class)))
})
public class UserApiController {

    private final UserService userService;

    @Value("${spring.servlet.multipart.location}")
    private String path;

    @Operation(summary = "유저 프로필 이미지 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 이미지 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "이미지 이름 응답",
                                    value = "{\"image\": \"12345678.jpg\"}"),
                    }
            )),

    })
    @PutMapping(value = "/user/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateProfileImage(
            @AuthUser PrincipalDetails principalDetails,
            @RequestParam("uploadfile") MultipartFile uploadfile
    ) {
        String imageName = userService.updateProfileImage(principalDetails.getId(), uploadfile);
        Map<String, Object> response = new HashMap<>();
        response.put("image", imageName);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "유저 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 이미지 수정 성공"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "닉네임 길이 제한",
                                            value = "{\"code\": 400, \"message\": \"Length must be less than 6                      \"}"),
                            }
                    )),
    })
    @PatchMapping(value = "/user")
    public ResponseEntity<UserInfoDto> updateUserInfo(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody UpdateUserDto updateUserDto
    ) {
        UserInfoDto userInfo = userService.update(principalDetails.getId(), updateUserDto.getNickname());
        return ResponseEntity.ok().body(userInfo);
    }

    @Operation(summary = "유저 정보 조회")
    @GetMapping(value = "/user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    public ResponseEntity<UserInfoDto> getUserInfo(
            @AuthUser PrincipalDetails principalDetails) {
        UserInfoDto userInfo = userService.getUserInfo(principalDetails.getId());
        return ResponseEntity.ok().body(userInfo);
    }

    @Operation(summary = "유저 거점 변경")
    @PutMapping(value = "/user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 변경 성공", content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "참여자가 아닐 경우",
                                            value = "{\"code\": 400, \"message\": \"Wrong dormitory name\"}"),
                            }
                    )),
    })
    public ResponseEntity<Map<String, Object>> setDormitory(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(description = "유저 거점 (KB | SUNGLIM | SULIM | BULAM | NURI)")
            @RequestParam("dormitory") String dormitoryName
    ) {
        User user = userService.updateDormitory(principalDetails.getId(), dormitoryName);
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return ResponseEntity.ok().body(response);
    }
}
