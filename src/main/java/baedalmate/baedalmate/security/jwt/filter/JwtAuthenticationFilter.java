package baedalmate.baedalmate.security.jwt.filter;

import baedalmate.baedalmate.errors.errorcode.UserErrorCode;
import baedalmate.baedalmate.security.jwt.service.JwtTokenProvider;
import baedalmate.baedalmate.security.jwt.service.PrincipalDetailsService;
import baedalmate.baedalmate.user.service.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalDetailsService principalDetailsService;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String servletPath = request.getServletPath();
//        if (servletPath.contains("/swagger-ui") || servletPath.contains("/room") || servletPath.equals("/api/refresh")) {
//            filterChain.doFilter(request, response);
//        }
        try {
            String jwt = getJwtFromRequest(request);
            if (redisService.getValues(jwt) != null) {
                throw new ExpiredJwtException(jwtTokenProvider.getHeaderFromToken(jwt), jwtTokenProvider.getClaimsFromToken(jwt), "Already logout");
            }
            String refreshToken = getRefreshTokenFromRequest(request);
            if (refreshToken == null && StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                UserDetails userDetails = principalDetailsService.loadUserByUsername(Long.toString(userId));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (SignatureException e) {
            request.setAttribute("exception", UserErrorCode.WRONG_TYPE_SIGNATURE);
        } catch (MalformedJwtException e) {
            request.setAttribute("exception", UserErrorCode.WRONG_TYPE_TOKEN);
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", UserErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            request.setAttribute("exception", UserErrorCode.WRONG_TYPE_TOKEN);
        } catch (IllegalArgumentException e) {
            request.setAttribute("exception", UserErrorCode.INVALID_ACCESS_TOKEN);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Refresh-Token");
    }
}
