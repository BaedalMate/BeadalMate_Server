package baedalmate.baedalmate.security.oauth2.client;

import baedalmate.baedalmate.config.FeignConfig;
import baedalmate.baedalmate.security.oauth2.dto.ApplePublicKeyResponse;
import baedalmate.baedalmate.security.oauth2.dto.AppleToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "appleClient", url = "https://appleid.apple.com/auth", configuration = FeignConfig.class)
public interface AppleClient {
    @GetMapping(value = "/keys")
    ApplePublicKeyResponse getAppleAuthPublicKey();

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    AppleToken.Response getToken(AppleToken.Request request);

}