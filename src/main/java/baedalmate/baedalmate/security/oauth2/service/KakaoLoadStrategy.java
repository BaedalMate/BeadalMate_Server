package baedalmate.baedalmate.security.oauth2.service;

import baedalmate.baedalmate.security.oauth2.dto.KakaoUserInfo;
import baedalmate.baedalmate.security.oauth2.exception.ExpiredAccessTokenException;
import baedalmate.baedalmate.security.oauth2.soical.SocialType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

public class KakaoLoadStrategy extends SocialLoadStrategy {

    protected KakaoUserInfo sendRequestToSocialSite(HttpEntity request) throws AuthenticationException {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(SocialType.KAKAO.getUserInfoUrl(),// -> /v2/user/me
                    SocialType.KAKAO.getMethod(),
                    request,
                    RESPONSE_TYPE);

            return new KakaoUserInfo(response.getBody());//카카오는 id를 PK로 사용

        } catch (HttpClientErrorException e) {
            throw new ExpiredAccessTokenException();
        }
    }
}