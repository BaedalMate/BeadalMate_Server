package baedalmate.baedalmate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();
    private final Redis redis = new Redis();
    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMsec;
        private long refreshTokenExpirationMsec;
        public String getTokenSecret() {
            return tokenSecret;
        }
        public void setTokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
        }
        public long getTokenExpirationMsec() {
            return tokenExpirationMsec;
        }
        public void setTokenExpirationMsec(long tokenExpirationMsec) {
            this.tokenExpirationMsec = tokenExpirationMsec;
        }
        public long getRefreshTokenExpirationMsec() {
            return refreshTokenExpirationMsec;
        }
        public void setRefreshTokenExpirationMsec(long refreshTokenExpirationMsec) {
            this.refreshTokenExpirationMsec = refreshTokenExpirationMsec;
        }
    }
    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();
        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }
        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }
    public static class Redis {
        private String host;
        private int port;
        public String getHost() {
            return host;
        }
        public int getPort() {
            return port;
        }
        public void setHost(String host) {
            this.host = host;
        }
        public void setPort(int port) {
            this.port = port;
        }
    }
    public Auth getAuth() {
        return auth;
    }
    public OAuth2 getOauth2() {
        return oauth2;
    }
    public Redis getRedis() {
        return redis;
    }
}
