package baedalmate.baedalmate.oauth.service.strategy;

import baedalmate.baedalmate.oauth.SocialType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class KakaoLoadStrategy extends SocialLoadStrategy{



    protected String sendRequestToSocialSite(HttpEntity request){
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(SocialType.KAKAO.getUserInfoUrl(),// -> /v2/user/me
                    SocialType.KAKAO.getMethod(),
                    request,
                    RESPONSE_TYPE);

            return response.getBody().get("id").toString();//카카오는 id를 PK로 사용

        } catch (Exception e) {
            throw e;
        }
    }
}