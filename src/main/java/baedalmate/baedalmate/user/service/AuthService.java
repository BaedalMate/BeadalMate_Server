package baedalmate.baedalmate.user.service;

import baedalmate.baedalmate.errors.exceptions.ExpiredRefreshTokenException;
import baedalmate.baedalmate.errors.exceptions.InvalidRefreshTokenException;
import baedalmate.baedalmate.security.jwt.service.JwtTokenProvider;
import baedalmate.baedalmate.user.dto.TokenDto;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public TokenDto refresh(String token, String refreshToken) {
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
        redisService.logout(token);
        redisService.delValues(refreshToken);
        Long userIdFromAccessToken;
        try {
            jwtTokenProvider.getUserIdFromExpiredToken(token.substring(7, token.length()));
        } catch (ExpiredJwtException e) {
            userIdFromAccessToken = Long.parseLong(e.getClaims().getSubject());
        }

        return new TokenDto(
                jwtTokenProvider.createToken(userId),
                jwtTokenProvider.createRefreshToken(userId)
        );
    }
}
