package baedalmate.baedalmate.security.oauth2.dto;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AppleUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public AppleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public AppleUserInfo(String name, String email, String socialId) {
        this.attributes = new HashMap<>();
        attributes.put("name", name);
        attributes.put("email", email);
        attributes.put("id", socialId);
    }

    @Override
    public String getSocialId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        LinkedHashMap<String, Object> kakaoAccount = (LinkedHashMap<String, Object>) attributes.get("email");
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getImage() {
        return "";
    }

    @Override
    public String getSocialType() {
        return "apple";
    }
}