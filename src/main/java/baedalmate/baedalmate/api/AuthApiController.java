package baedalmate.baedalmate.api;

import baedalmate.baedalmate.oauth.provider.TokenProvider;
import baedalmate.baedalmate.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"인증 api"})
@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @ApiOperation("로그인")
    @PostMapping("/login/oauth2/kakao")
    public tokenDto fakeLogin(@RequestBody LoginDto loginDto) {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
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
