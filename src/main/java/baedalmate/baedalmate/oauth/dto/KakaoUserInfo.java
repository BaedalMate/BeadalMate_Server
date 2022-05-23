package baedalmate.baedalmate.oauth.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getSocialId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        LinkedHashMap<String, Object> kakaoAccount = (LinkedHashMap<String, Object>) attributes.get("kakao_account");
        LinkedHashMap<String, String> profile = (LinkedHashMap<String, String>) kakaoAccount.get("profile");
        return profile.get("nickname");
    }

    @Override
    public String getEmail() {
        LinkedHashMap<String, Object> kakaoAccount = (LinkedHashMap<String, Object>) attributes.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getImage() {
        LinkedHashMap<String, Object> kakaoAccount = (LinkedHashMap<String, Object>) attributes.get("kakao_account");
        LinkedHashMap<String, String> profile = (LinkedHashMap<String, String>) kakaoAccount.get("profile");
        return profile.get("profile_image_url");
    }

    @Override
    public String getSocialType() {
        return "kakao";
    }
}
