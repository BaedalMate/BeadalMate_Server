package baedalmate.baedalmate.oauth.provider;


import baedalmate.baedalmate.config.AppProperties;
import baedalmate.baedalmate.domain.User;
import baedalmate.baedalmate.oauth.authentication.OAuth2UserDetails;
import baedalmate.baedalmate.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private final AppProperties appProperties;

    @Autowired
    public UserRepository userRepository;
    public String createToken(Authentication authentication) {
        OAuth2UserDetails principalDetails = (OAuth2UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());
        User user = userRepository.findBySocialTypeAndSocialId(principalDetails.getSocialType(), principalDetails.getSocialId());
        return Jwts.builder()
                .setSubject(Long.toString(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        Date refreshTokenExpiryDate = new Date(now.getTime() + appProperties.getAuth().getRefreshTokenExpirationMsec());
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
    public boolean validateToken(String authToken) throws ExpiredJwtException {
//        try {
            Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
            return true;
//        } catch (SignatureException ex) {
//            logger.error("Invalid JWT signature");
//        } catch (MalformedJwtException ex) {
//            logger.error("Invalid JWT token");
//        } catch (ExpiredJwtException ex) {
//            logger.error("Expired JWT token");
//        } catch (UnsupportedJwtException ex) {
//            logger.error("Unsupported JWT token");
//        } catch (IllegalArgumentException ex) {
//            logger.error("JWT claims string is empty.");
//        }
//        return false;
    }
}