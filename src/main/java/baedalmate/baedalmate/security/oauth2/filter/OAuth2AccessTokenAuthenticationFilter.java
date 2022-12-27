package baedalmate.baedalmate.security.oauth2.filter;

import baedalmate.baedalmate.security.oauth2.authentication.AccessTokenSocialTypeToken;
import baedalmate.baedalmate.security.oauth2.provider.AccessTokenAuthenticationProvider;
import baedalmate.baedalmate.security.oauth2.soical.SocialType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class OAuth2AccessTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DEFAULT_OAUTH2_LOGIN_REQUEST_URL_PREFIX = "/login/oauth2/";  // /login/oauth2/ + ????? 로 오는 요청을 처리할 것이다

    private static final String HTTP_METHOD = "POST";    //HTTP 메서드의 방식은 POST이다.

    private static final AntPathRequestMatcher DEFAULT_OAUTH2_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_OAUTH2_LOGIN_REQUEST_URL_PREFIX + "*", HTTP_METHOD); //=>   /login/oauth2/* 의 요청에, POST으로 온 요청에 매칭된다.

    public OAuth2AccessTokenAuthenticationFilter(AccessTokenAuthenticationProvider accessTokenAuthenticationProvider,   //Provider를 등록
                                                 AuthenticationSuccessHandler authenticationSuccessHandler,  //로그인 성공 시 처리할  handler이다
                                                 AuthenticationFailureHandler authenticationFailureHandler) { //로그인 실패 시 처리할 handler이다.

        super(DEFAULT_OAUTH2_LOGIN_PATH_REQUEST_MATCHER);   // 위에서 설정한  /oauth2/login/* 의 요청에, POST으로 온 요청을 처리하기 위해 설정한다.

        this.setAuthenticationManager(new ProviderManager(accessTokenAuthenticationProvider));
        //AbstractAuthenticationProcessingFilter를 커스터마이징 하려면  ProviderManager를 꼭 지정해 주어야 한다(안그러면 예외남!!!)

        this.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        this.setAuthenticationFailureHandler(authenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //AbstractAuthenticationProcessingFilter 의 추상 메서드를 구현한다. Authentication 객체를 반환해야 한다.

        SocialType socialType = extractSocialType(request);
        //어떤 소셜 로그인을 진행할 것인지를 uri롤 통해 추출한다. kakao, google, naver가 있으며, 예를 들어 /oauth2/login/kakao로 요청을 보내면 kakao를 추출한다

        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);


        String accessToken = "";  //헤더의 AccessToken에 해당하는 값을 가져온다.
        String appleIdentityToken = "";
        String appleAuthorizationCode = "";
        String username = "";
        String email = "";

        switch (socialType.getSocialName()) {
            case ("kakao"):
                SocialAccessToken socialAccessToken = objectMapper.readValue(messageBody, SocialAccessToken.class);
                accessToken = socialAccessToken.getKakaoAccessToken();
                break;
            case ("apple"):
                AppleAccessToken appleAccessToken = objectMapper.readValue(messageBody, AppleAccessToken.class);
                appleIdentityToken = appleAccessToken.getAppleIdentityToken();
                appleAuthorizationCode = appleAccessToken.getAppleAuthorizationCode();
                username = appleAccessToken.getUserName();
                email = appleAccessToken.getEmail();
            default:
        }
        if (socialType.getSocialName() == "apple") {
            return this.getAuthenticationManager().authenticate(new AccessTokenSocialTypeToken(appleIdentityToken, appleAuthorizationCode, email, username, socialType));
        }
        return this.getAuthenticationManager().authenticate(new AccessTokenSocialTypeToken(accessToken, socialType));
        //AuthenticationManager에게 인증 요청을 보낸다. 이때 Authentication 객체로는 AccessTokenSocialTypeToken을(직접 커스텀 함) 사용한다.
    }

    private SocialType extractSocialType(HttpServletRequest request) {//요청을 처리하는 코드이다
        return Arrays.stream(SocialType.values())//SocialType.values() -> GOOGLE, KAKAO, NAVER 가 있다.
                .filter(socialType ->
                        socialType.getSocialName()
                                .equals(request.getRequestURI().substring(DEFAULT_OAUTH2_LOGIN_REQUEST_URL_PREFIX.length())))
                //subString을 통해 문자열을 잘라주었다. 해당 코드를 실행하면 ~~~/kakao에서 kakao만 추출된다
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 URL 주소입니다"));
    }

    @Data
    static class SocialAccessToken {
        private String kakaoAccessToken;
    }

    @Data
    static class AppleAccessToken {
        private String appleIdentityToken;
        private String appleAuthorizationCode;
        private String userName;
        private String email;
    }
}