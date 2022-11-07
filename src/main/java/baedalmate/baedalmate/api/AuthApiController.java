package baedalmate.baedalmate.api;

import baedalmate.baedalmate.dto.TokenResponse;
import baedalmate.baedalmate.repository.UserRepository;
import baedalmate.baedalmate.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.headers.Header;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"인증 api"})
@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    @ApiOperation("로그인")
    @PostMapping("/login/oauth2/kakao")
    public tokenDto fakeLogin(@RequestBody LoginDto loginDto) {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
    }

    @ApiOperation("토큰 재발급")
    @PostMapping("/api/v1/refresh")
    public TokenResponse refreshToken(
            @RequestHeader(value = "Authorization") String token,
            @RequestHeader(value = "Refresh-Token") String refreshToken) {

        return authService.refresh(token.substring(7, token.length()), refreshToken);
    }

    @Data
    static class LoginDto {
        private String kakaoAccessToken;
    }

    @Data
    static class tokenDto {
        private String accessToken;
        private String refreshToken;
    }
}
