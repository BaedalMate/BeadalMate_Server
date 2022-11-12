package baedalmate.baedalmate.security.oauth2.handler;

import baedalmate.baedalmate.config.AppProperties;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.security.jwt.service.JwtTokenProvider;
import baedalmate.baedalmate.security.repository.AuthRepository;
import baedalmate.baedalmate.security.user.OAuth2UserDetails;
import baedalmate.baedalmate.user.dto.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2UserDetails principalDetails = (OAuth2UserDetails) authentication.getPrincipal();
        User user = authRepository.findBySocialTypeAndSocialId(principalDetails.getSocialType(), principalDetails.getSocialId()).get();

        String accessToken = jwtTokenProvider.createToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        if (jsonConverter.canWrite(tokenDto.getClass(), jsonMimeType)) {
            jsonConverter.write(tokenDto, jsonMimeType, new ServletServerHttpResponse(response));
        }
    }
}
