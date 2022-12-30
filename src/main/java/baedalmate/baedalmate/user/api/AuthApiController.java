package baedalmate.baedalmate.user.api;

import baedalmate.baedalmate.swagger.AccessDeniedErrorResponseDto;
import baedalmate.baedalmate.swagger.ExpiredJwtErrorResponseDto;
import baedalmate.baedalmate.swagger.ExpiredSocialTokenErrorResponseDto;
import baedalmate.baedalmate.user.dto.AppleLoginDto;
import baedalmate.baedalmate.user.service.AuthService;
import baedalmate.baedalmate.user.dto.LoginDto;
import baedalmate.baedalmate.user.dto.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 api")
@RestController
@RequiredArgsConstructor
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
})
public class AuthApiController {

    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/login/oauth2/kakao")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
    })
    public TokenDto fakeLogin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "카카오 로그인",
                                            value = "{\"kakaoAccessToken\": \"string\"}"),
                                    @ExampleObject(name = "애플 로그인",
                                            value = "{\"appleIdentityCode\": \"string\", \"appleAuthorizationCode\": \"string\", \"userName\": \"string\", \"email\": \"string\"}"),
                            })) AppleLoginDto appleLoginDto) {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/api/v1/refresh")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "400", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "이미 사용된 refresh token",
                                    value = "{\"code\": 400, \"message\": \"Refresh token doesn't exist\"}"),
                            @ExampleObject(name = "만료된 refresh token",
                                    value = "{\"code\": 400, \"message\": \"Expired refresh token\"}"),
                    }
            ))
    })
    public TokenDto refreshToken(
            @RequestHeader(value = "Authorization") String token,
            @RequestHeader(value = "Refresh-Token") String refreshToken) {

        return authService.refresh(token.substring(7, token.length()), refreshToken);
    }
}
