package baedalmate.baedalmate.user.dto;

import lombok.Data;

@Data
public class AppleLoginDto {
    private String appleIdentityToken;
    private String appleAuthorizationCode;
    private String userName;
    private String email;
}
