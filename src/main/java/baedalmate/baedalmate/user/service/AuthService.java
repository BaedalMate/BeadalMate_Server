package baedalmate.baedalmate.user.service;

import baedalmate.baedalmate.errors.exceptions.ExpiredRefreshTokenException;
import baedalmate.baedalmate.errors.exceptions.InvalidRefreshTokenException;
import baedalmate.baedalmate.security.jwt.service.JwtTokenProvider;
import baedalmate.baedalmate.user.dao.FcmJpaRepository;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.Fcm;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.user.dto.TokenDto;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final FcmJpaRepository fcmJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public void updateFcm(Long userId, String fcmToken, String deviceCode) {
        Fcm fcm = fcmJpaRepository.findAllByUserIdAndDeviceCode(userId, deviceCode);
        if(fcm == null) {
            User user = userJpaRepository.findById(userId).get();
            fcm = Fcm.createFcm(user, fcmToken, deviceCode);
            fcmJpaRepository.save(fcm);
        } else {
            fcmJpaRepository.updateFcmTokenByUserIdAndDeviceCode(userId, deviceCode, fcmToken);
        }
    }

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
            jwtTokenProvider.getUserIdFromExpiredToken(token);
        } catch (ExpiredJwtException e) {
            userIdFromAccessToken = Long.parseLong(e.getClaims().getSubject());
        }

        return new TokenDto(
                jwtTokenProvider.createToken(userId),
                jwtTokenProvider.createRefreshToken(userId)
        );
    }
}
