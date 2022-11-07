package baedalmate.baedalmate.security.oauth2.exception;

import org.springframework.security.core.AuthenticationException;

public class ExpiredAccessTokenException extends AuthenticationException {
    public ExpiredAccessTokenException() {
        super("this access token is already expired");
    }
}
