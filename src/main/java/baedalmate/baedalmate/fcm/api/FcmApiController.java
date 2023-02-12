package baedalmate.baedalmate.fcm.api;

import baedalmate.baedalmate.fcm.dto.FcmAllowDto;
import baedalmate.baedalmate.fcm.service.FcmService;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.swagger.ExpiredJwtErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "푸시 알림 api")
@RestController
@RequiredArgsConstructor
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
})
@RequestMapping("/api/v1")
public class FcmApiController {

    private final FcmService fcmService;

    @Operation(summary = "fcm 토큰 등록")
    @PostMapping("/fcm")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "fcm 토큰 성공")
    })
    public ResponseEntity<Map<String, Object>> fcmToken(
            @Parameter(description = "fcm token") @RequestHeader(value = "Fcm-Token") String fcmToken,
            @Parameter(description = "기기 정보") @RequestHeader(value = "Device-Code") String deviceCode,
            @AuthUser PrincipalDetails principalDetails) {

        fcmService.saveOrGetFcm(principalDetails.getId(), fcmToken, deviceCode);
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "fcm 알림 허용 여부 설정")
    @PutMapping("/fcm")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "fcm 토큰 성공"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "해당 디바이스 fcm 존재하지 않음",
                                            value = "{\"code\": 400, \"message\": \"Fcm token doesn't exist\"}")
                            }
                    )),
    })
    public ResponseEntity<FcmAllowDto> fcmAllow(
            @Parameter(description = "기기 정보") @RequestHeader(value = "Device-Code") String deviceCode,
            @Parameter(description = "채팅 알림 설정") @RequestParam(value = "allow_chat", required = false) Boolean allowChat,
            @Parameter(description = "모집글 알림 설정") @RequestParam(value = "allow_recruit", required = false) Boolean allowRecruit,
            @AuthUser PrincipalDetails principalDetails) {

        FcmAllowDto response = fcmService.updateFcm(principalDetails.getId(), deviceCode, allowChat, allowRecruit);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "fcm 알림 허용 여부 조회")
    @GetMapping("/fcm")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "fcm 토큰 성공"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "해당 디바이스 fcm 존재하지 않음",
                                            value = "{\"code\": 400, \"message\": \"Fcm token doesn't exist\"}")
                            }
                    )),
    })
    public ResponseEntity<FcmAllowDto> fcmAllow(
            @Parameter(description = "기기 정보") @RequestHeader(value = "Device-Code") String deviceCode,
            @AuthUser PrincipalDetails principalDetails) {

        FcmAllowDto response = fcmService.findOne(principalDetails.getId(), deviceCode);
        return ResponseEntity.ok().body(response);
    }
}
