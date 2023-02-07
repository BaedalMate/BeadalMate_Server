package baedalmate.baedalmate.security.logout;

import baedalmate.baedalmate.security.jwt.service.JwtTokenProvider;
import baedalmate.baedalmate.security.user.OAuth2UserDetails;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.user.dao.FcmJpaRepository;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.user.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final RedisService redisService;
    private final FcmJpaRepository fcmJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws AuthenticationException {
        SecurityContextHolder.clearContext();
        String accessToken = getJwtFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        String deviceCode = getDeviceCodeFromRequest(request);
        if(deviceCode != null) {
            fcmJpaRepository.deleteByDeviceCode(deviceCode, userId);
        }
        redisService.logout(accessToken);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    private String getDeviceCodeFromRequest(HttpServletRequest request) {
        return request.getHeader("Device-Code");
    }
}
