package baedalmate.baedalmate.user.api;

import baedalmate.baedalmate.user.service.AuthService;
import baedalmate.baedalmate.user.dto.LoginDto;
import baedalmate.baedalmate.user.dto.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 api")
@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/login/oauth2/kakao")
    public TokenDto fakeLogin(@RequestBody LoginDto loginDto) {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/api/v1/refresh")
    public TokenDto refreshToken(
            @RequestHeader(value = "Authorization") String token,
            @RequestHeader(value = "Refresh-Token") String refreshToken) {

        return authService.refresh(token.substring(7, token.length()), refreshToken);
    }
}
