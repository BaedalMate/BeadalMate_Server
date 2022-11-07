package baedalmate.baedalmate.api;

import baedalmate.baedalmate.dto.TokenResponse;
import baedalmate.baedalmate.errors.exceptions.ExpiredRefreshTokenException;
import baedalmate.baedalmate.errors.exceptions.InvalidRefreshTokenException;
import baedalmate.baedalmate.repository.UserRepository;
import baedalmate.baedalmate.security.jwt.service.JwtTokenProvider;
import baedalmate.baedalmate.service.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @ApiOperation("로그인")
    @PostMapping("/login/oauth2/kakao")
    public tokenDto fakeLogin(@RequestBody LoginDto loginDto) {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
    }

    @PostMapping("/api/v1/refresh")
    public TokenResponse refreshToken(
            @RequestHeader(value = "Authorization") String token,
            @RequestHeader(value = "Refresh-Token") String refreshToken) {

        Long userId;
        if (jwtTokenProvider.existsRefreshToken(refreshToken)) {
            userId = Long.valueOf(redisService.getValues(refreshToken));
        } else {
            throw new InvalidRefreshTokenException();
        }

        try {
            jwtTokenProvider.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ExpiredRefreshTokenException();
        }
        redisService.logout(token.substring(7, token.length()));
        redisService.delValues(refreshToken);
        Long userIdFromAccessToken;
        try {
            jwtTokenProvider.getUserIdFromExpiredToken(token.substring(7, token.length()));
        } catch (ExpiredJwtException e) {
            userIdFromAccessToken = Long.parseLong(e.getClaims().getSubject());
        }

        return new TokenResponse(
                jwtTokenProvider.createToken(userId),
                jwtTokenProvider.createRefreshToken(userId)
        );
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
