package baedalmate.baedalmate.security.jwt.service;

import baedalmate.baedalmate.config.AppProperties;
import baedalmate.baedalmate.security.repository.AuthRepository;
import baedalmate.baedalmate.service.RedisService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final AppProperties appProperties;

    private final AuthRepository authRepository;
    private final RedisService redisService;

    public String createToken(Long id) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());
        return Jwts.builder()
                .setSubject(Long.toString(id))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
                .compact();
    }

    @Transactional
    public String createRefreshToken(Long id) {
        Date now = new Date();
        Date refreshTokenExpiryDate = new Date(now.getTime() + appProperties.getAuth().getRefreshTokenExpirationMsec());

        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
                .compact();

        redisService.setValues(refreshToken, String.valueOf(id));

        return refreshToken;
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public Long getUserIdFromExpiredToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    // RefreshToken 존재유무 확인
    public boolean existsRefreshToken(String refreshToken) {
        return redisService.getValues(refreshToken) != null;
        //// return tokenRepository.existsByRefreshToken(refreshToken);
    }

    public boolean validateToken(String authToken) throws SecurityException, JwtException {
        Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
        return true;
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(appProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public Header getHeaderFromToken(String token) {
        return Jwts.header();
    }
}