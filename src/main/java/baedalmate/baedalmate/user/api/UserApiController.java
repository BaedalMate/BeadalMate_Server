package baedalmate.baedalmate.user.api;

import baedalmate.baedalmate.recruit.dto.HostedRecruitDto;
import baedalmate.baedalmate.recruit.dto.ParticipatedRecruitDto;
import baedalmate.baedalmate.recruit.dto.RecruitListDto;
import baedalmate.baedalmate.recruit.dto.RecruitListWithLastDto;
import baedalmate.baedalmate.recruit.service.RecruitService;
import baedalmate.baedalmate.swagger.*;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.user.dto.UpdateDormitoryDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
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
    private final RecruitService recruitService;

    @Value("${spring.servlet.multipart.location}")
    private String path;

    @Operation(summary = "회원 탈퇴(비활성화)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴(비활성화) 성공", content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "참여 중인 모집글 존재",
                                            value = "{\"code\": 400, \"message\": \"User is participating some recruit.\"}"),
                            }
                    )),
    })
    @GetMapping(value = "/user/deactivate")
    public ResponseEntity<Map> deactivate(
            @AuthUser PrincipalDetails principalDetails
    ) {
        userService.deactivate(principalDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "참여한 모집글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ParticipatedRecruitListResponseDto.class))),

    })
    @GetMapping(value = "/user/participated-recruit")
    @CustomPageableAsQueryParam
    public ResponseEntity<RecruitListWithLastDto> participatedRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(hidden = true)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "createDate", direction = Sort.Direction.ASC)
            })
            Pageable pageable
    ) {
        Page<ParticipatedRecruitDto> participatedRecruitDto = recruitService.findParticipatedRecruit(principalDetails.getId(), pageable);
        RecruitListWithLastDto response = new RecruitListWithLastDto(participatedRecruitDto.getContent(), participatedRecruitDto.isLast());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "주최한 모집글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = HostedRecruitListResponseDto.class))),

    })
    @CustomPageableAsQueryParam
    @GetMapping(value = "/user/hosted-recruit")
    public ResponseEntity<RecruitListWithLastDto> hostedRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(hidden = true)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "createDate", direction = Sort.Direction.ASC)
            })
                    Pageable pageable
    ) {
        Page<HostedRecruitDto> hostedRecruitDtos = recruitService.findHostedRecruit(principalDetails.getId(), pageable);
        RecruitListWithLastDto response = new RecruitListWithLastDto(hostedRecruitDtos.getContent(), hostedRecruitDtos.isLast());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "유저 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 정보 수정 성공"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "닉네임 길이 제한",
                                            value = "{\"code\": 400, \"message\": \"Length must be less than 6\"}"),
                            }
                    )),
    })
    @PutMapping(value = "/user")
    public ResponseEntity<UserInfoDto> updateUserInfo(
            @AuthUser PrincipalDetails principalDetails,
            @RequestParam(value = "uploadfile", required = false) MultipartFile uploadfile,
            @Parameter(description = "기본 이미지로 변경할 때만 true 처리. 파일 존재 여부보다 우선이기 때문에 파일과 함께 true를 보내면 기본이미지로 변경됩니다.")
            @RequestParam(value = "default_image", required = false, defaultValue = "false") boolean isDefaultImage,
            @RequestParam(value = "nickname", required = false) String nickname
    ) {
        UserInfoDto userInfo = userService.update(principalDetails.getId(), nickname, isDefaultImage, uploadfile);
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
    @PutMapping(value = "/user/dormitory")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 변경 성공", content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "잘못된 기숙사 이름",
                                            value = "{\"code\": 400, \"message\": \"Wrong dormitory name\"}"),
                            }
                    )),
    })
    public ResponseEntity<Map<String, Object>> setDormitory(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody UpdateDormitoryDto updateDormitoryDto
    ) {
        User user = userService.updateDormitory(principalDetails.getId(), updateDormitoryDto.getDormitory());
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return ResponseEntity.ok().body(response);
    }
}
