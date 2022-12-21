package baedalmate.baedalmate.security.oauth2.exception;

import org.springframework.security.core.AuthenticationException;

public class RsaNotFoundException extends AuthenticationException {
    public RsaNotFoundException() {
        super("Can not find available rsa");
    };
}
